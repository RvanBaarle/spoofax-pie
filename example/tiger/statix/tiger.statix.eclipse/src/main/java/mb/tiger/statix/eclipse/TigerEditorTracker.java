package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.EclipseIdentifiers;
import mb.spoofax.eclipse.editor.EditorTracker;

import javax.inject.Inject;

@mb.tiger.statix.spoofax.TigerScope
public class TigerEditorTracker extends EditorTracker {
    @Inject public TigerEditorTracker(EclipseIdentifiers eclipseIdentifiers) {
        super(eclipseIdentifiers);
    }
}
