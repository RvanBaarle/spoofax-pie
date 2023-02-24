#include "gc.h"
#include "debug.h"
#include "GarbageCollector.h"

[[maybe_unused]]
void gc_init() {
    // NOOP
}

[[maybe_unused]]
void *gc_alloc(uint64_t size, bool has_pointers) {
    DEBUG_LOG("GC_ALLOC");
    return garbageCollector.allocate(size, has_pointers ? ARRAY : INT_ARRAY);
}

[[maybe_unused]]
void *gc_alloc_bitfield(uint64_t size, uint64_t bitfield) {
    DEBUG_LOG("GC_ALLOC_BITFIELD");
    return garbageCollector.allocate_bitfield(size, bitfield);
}

[[maybe_unused]]
void gc_mark_has_pointers(uint64_t *object) {
    garbageCollector.mark_has_pointers(object);
}

[[maybe_unused]]
void gc_collect() {
    garbageCollector.collect();
}