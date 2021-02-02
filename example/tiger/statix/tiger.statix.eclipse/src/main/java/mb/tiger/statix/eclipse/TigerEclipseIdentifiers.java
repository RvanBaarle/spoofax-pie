package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.EclipseIdentifiers;

@mb.tiger.statix.spoofax.TigerScope
public class TigerEclipseIdentifiers implements EclipseIdentifiers {
    @Override public String getPlugin() {
        return "tiger.statix.eclipse";
    }

    @Override public String getContext() {
        return "tiger.statix.eclipse.context";
    }

    @Override public String getDocumentProvider() {
        return "tiger.statix.eclipse.documentprovider";
    }

    @Override public String getEditor() {
        return "tiger.statix.eclipse.editor";
    }

    @Override public String getNature() {
        return "tiger.statix.eclipse.nature";
    }

    @Override public String getProjectBuilder() {
        return "tiger.statix.eclipse.builder";
    }


    @Override public String getBaseMarker() {
        return "tiger.statix.eclipse.marker";
    }

    @Override public String getInfoMarker() {
        return "tiger.statix.eclipse.marker.info";
    }

    @Override public String getWarningMarker() {
        return "tiger.statix.eclipse.marker.warning";
    }

    @Override public String getErrorMarker() {
        return "tiger.statix.eclipse.marker.error";
    }


    @Override public String getAddNatureCommand() {
        return "tiger.statix.eclipse.nature.add";
    }

    @Override public String getRemoveNatureCommand() {
        return "tiger.statix.eclipse.nature.remove";
    }

    @Override public String getObserveCommand() {
        return "tiger.statix.eclipse.observe";
    }

    @Override public String getUnobserveCommand() {
        return "tiger.statix.eclipse.unobserve";
    }

    @Override public String getRunCommand() {
        return "tiger.statix.eclipse.runcommand";
    }


    @Override public String getResourceContextMenu() {
        return "tiger.statix.eclipse.menu.resource.context";
    }

    @Override public String getEditorContextMenu() {
        return "tiger.statix.eclipse.menu.editor.context";
    }

    @Override public String getMainMenu() {
        return "tiger.statix.eclipse.menu.main";
    }

    @Override public String getMainMenuDynamic() {
        return "tiger.statix.eclipse.menu.main.dynamic";
    }
}
