package mb.esv.util;

import mb.common.option.Option;
import mb.common.result.Result;
import mb.common.text.Text;
import mb.common.util.ListView;
import mb.esv.task.spoofax.EsvParseWrapper;
import mb.jsglr.common.JsglrParseException;
import mb.jsglr.common.JsglrParseOutput;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.SupplierWithOrigin;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.resource.ReadableResource;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.resource.ResourceTextSupplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;

public abstract class EsvVisitor {
    private final EsvParseWrapper parse;
    private final ListView<Supplier<Result<ResourcePath, ?>>> includeDirectorySuppliers;
    private final ListView<Supplier<Result<IStrategoTerm, ?>>> includeAstSuppliers;

    protected EsvVisitor(
        EsvParseWrapper parse,
        ListView<Supplier<Result<ResourcePath, ?>>> includeDirectorySuppliers,
        ListView<Supplier<Result<IStrategoTerm, ?>>> includeAstSuppliers
    ) {
        this.parse = parse;
        this.includeDirectorySuppliers = includeDirectorySuppliers;
        this.includeAstSuppliers = includeAstSuppliers;
    }


    protected void acceptAst(IStrategoTerm ast) {}

    protected void acceptIncludeDirectorySupplyFail(IStrategoTerm importTerm, String importName, Exception e) {}

    protected void acceptIncludeAstSupplyFail(IStrategoTerm importTerm, String importName, Exception e) {}

    protected void acceptUnresolvedImport(IStrategoTerm importTerm, String importName) {}

    protected void acceptParseFail(JsglrParseException parseException) {}

    protected void acceptParse(JsglrParseOutput parseOutput) {}


    public void visitMainFile(
        ExecContext context,
        ResourceKey mainFile,
        ResourcePath rootDirectory
    ) {
        parse(context, mainFile, rootDirectory, null).ifSomeThrowing(ast -> visitAst(context, new HashSet<>(), rootDirectory, ast));
    }

    public void visitAst(
        ExecContext context,
        IStrategoTerm ast,
        ResourcePath rootDirectory
    ) {
        final HashSet<String> seenImports = new HashSet<>();
        visitAst(context, seenImports, rootDirectory, ast);
    }

    private void visitAst(
        ExecContext context,
        HashSet<String> seenModules,
        ResourcePath rootDirectory,
        IStrategoTerm ast
    ) {
        if(!EsvUtil.isModuleTerm(ast)) throw new RuntimeException("AST '" + ast + "' is not a Module/3 term");
        acceptAst(ast);
        seenModules.add(EsvUtil.getNameFromModuleTerm(ast));
        final IStrategoTerm importsTerm = ast.getSubterm(1);
        if(EsvUtil.isImportsTerm(importsTerm)) {
            for(IStrategoTerm importTerm : importsTerm.getSubterm(0)) {
                final String importName = EsvUtil.getNameFromImportTerm(importTerm);
                if(seenModules.contains(importName)) continue; // Short-circuit cyclic imports.
                resolveImport(context, rootDirectory, importTerm, importName).ifSome(importedAst -> visitAst(context, seenModules, rootDirectory, importedAst));
            }
        }
    }

    private Option<IStrategoTerm> resolveImport(ExecContext context, ResourcePath rootDirectory, IStrategoTerm importTerm, String importName) {
        for(Supplier<Result<ResourcePath, ?>> includeDirectorySupplier : includeDirectorySuppliers) {
            final Result<ResourcePath, ?> result = context.require(includeDirectorySupplier);
            if(result.isErr()) {
                acceptIncludeDirectorySupplyFail(importTerm, importName, result.getErr());
                continue;
            }
            final ResourcePath includeDirectory = result.get();
            final ResourcePath esvFile = includeDirectory.appendRelativePath(importName).ensureLeafExtension(EsvUtil.fileExtension).getNormalized();
            try {
                final ReadableResource resource = context.require(esvFile, ResourceStampers.<ReadableResource>exists());
                if(!resource.exists()) continue;
                return parse(context, esvFile, rootDirectory, includeDirectorySupplier);
            } catch(IOException e) {
                throw new UncheckedIOException(e); // Throw exceptions about existence check as unchecked.
            }
        }
        for(Supplier<Result<IStrategoTerm, ?>> includeAstSupplier : includeAstSuppliers) {
            final Result<IStrategoTerm, ?> result = context.require(includeAstSupplier);
            if(result.isErr()) {
                acceptIncludeAstSupplyFail(importTerm, importName, result.getErr());
                continue;
            }
            final IStrategoTerm ast = result.get();
            final String moduleName = EsvUtil.getNameFromModuleTerm(ast);
            if(importName.equals(moduleName)) {
                return Option.ofSome(ast);
            }
        }
        acceptUnresolvedImport(importTerm, importName);
        return Option.ofNone();
    }

    private Option<IStrategoTerm> parse(ExecContext context, ResourceKey file, ResourcePath rootDirectory, @Nullable Supplier<?> origin) {
        Supplier<Text> supplier = new ResourceTextSupplier(file);
        if(origin != null) {
            supplier = new SupplierWithOrigin<>(supplier, origin);
        }
        final Result<JsglrParseOutput, JsglrParseException> parseResult = context.require(parse, parse.inputBuilder()
            .textSupplier(supplier)
            .fileHint(file)
            .rootDirectoryHint(rootDirectory)
            .build()
        );
        if(parseResult.isErr()) {
            acceptParseFail(parseResult.getErr());
            return Option.ofNone();
        } else {
            final JsglrParseOutput output = parseResult.get();
            acceptParse(output);
            return Option.ofSome(output.ast);
        }
    }
}
