package normativecontrol.core.predefined

import normativecontrol.core.traits.Trait

interface ChapterHeaderTrait : Trait {
    val chapterHeaderHandler: AbstractChapterHeaderTraitImplementor
}