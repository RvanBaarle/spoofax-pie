package mb.jsglr.common;

import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;

public class JsglrParseInput implements Serializable {
    public final String text;
    public final String startSymbol;
    public final @Nullable ResourceKey fileHint;
    public final @Nullable ResourcePath rootDirectoryHint;

    public JsglrParseInput(String text, String startSymbol, @Nullable ResourceKey fileHint, @Nullable ResourcePath rootDirectoryHint) {
        this.text = text;
        this.startSymbol = startSymbol;
        this.fileHint = fileHint;
        this.rootDirectoryHint = rootDirectoryHint;
    }

    public JsglrParseInput(String text, String startSymbol, @Nullable ResourceKey fileHint) {
        this(text, startSymbol, fileHint, null);
    }

    public JsglrParseInput(String text, String startSymbol) {
        this(text, startSymbol, null);
    }

    @Override public boolean equals(@Nullable Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final JsglrParseInput that = (JsglrParseInput)o;
        if(!text.equals(that.text)) return false;
        if(!startSymbol.equals(that.startSymbol)) return false;
        if(fileHint != null ? !fileHint.equals(that.fileHint) : that.fileHint != null) return false;
        return rootDirectoryHint != null ? rootDirectoryHint.equals(that.rootDirectoryHint) : that.rootDirectoryHint == null;
    }

    @Override public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + startSymbol.hashCode();
        result = 31 * result + (fileHint != null ? fileHint.hashCode() : 0);
        result = 31 * result + (rootDirectoryHint != null ? rootDirectoryHint.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "JsglrParseInput{" +
            "text='" + text + '\'' +
            ", startSymbol='" + startSymbol + '\'' +
            ", fileHint=" + fileHint +
            ", rootDirectoryHint=" + rootDirectoryHint +
            '}';
    }
}