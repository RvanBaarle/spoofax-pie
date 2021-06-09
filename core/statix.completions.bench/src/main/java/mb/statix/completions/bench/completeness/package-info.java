// @formatter:off
@Value.Style(
    typeImmutable = "*",
    get = { "is*", "get*" },
    defaults = @Value.Immutable(builder = false, copy = true, prehash = true),
    // prevent generation of javax.annotation.*; bogus entry, because empty list = allow all
    allowedClasspathAnnotations = {Override.class}
)
// @formatter:on
package mb.statix.completions.bench.completeness;

import org.immutables.value.Value;
