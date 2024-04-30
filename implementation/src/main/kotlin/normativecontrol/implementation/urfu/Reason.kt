package normativecontrol.implementation.urfu

import normativecontrol.core.mistakes.MistakeReason

internal enum class Reason(override val description: String) : MistakeReason {
    ChapterOrderMismatch("Неверный порядок разделов"),
    IncorrectLineSpacingHeader("Некорректный межстрочный интервал заголовка"),
    IncorrectLineSpacingText("Некорректный межстрочный интервал текста"),
    TabInList("Запрещено использование табуляции в качестве разделителя после маркера списка. Методические указания, п. 6.3"),
    ForbiddenMarkerTypeLevel1("Данный вид маркера списка запрещен. Используйте ненумерованный список либо русские строчные буквы. Методические указания, п. 6.3"),
    ForbiddenMarkerTypeReferences("Данный вид маркера списка запрещен. Используйте нумерованный список. Методические указания, п. 6.10"),
    ReferenceNotMentionedInText("Не найдено ссылок для этого источника. Если они есть, убедитесь, что ссылки соответствуют правилам оформления. Методические указания, п. 6.10"),
    LeftIndentOnHeader("Обнаружен отступ слева у заголовка раздела."),
    LeftIndentOnText("Обнаружен отступ слева у текста."),
    LeftIndentOnPictureDescription("Обнаружен отступ слева у подписи рисунка."),
    RightIndentOnHeader("Обнаружен отступ справа у заголовка раздела."),
    RightIndentOnText("Обнаружен отступ справа у текста."),
    IncorrectFirstLineIndentInText("Неверный отступ первой строки у текста"),
    IncorrectFirstLineIndentInHeader("Неверный отступ первой строки у заголовка"),
    IncorrectFirstLineIndentInPictureDescription("Неверный отступ первой строки у подписи рисунка"),
    IncorrectFirstLineIndentInTableTitle("Неверный отступ первой строки у заголовка таблицы"),
    IncorrectLeftIndentInList("Неверный отступ слева в списке"),
    IncorrectJustifyOnBodyHeader("Заголовок раздела основной части должен быть выровнен по ширине"),
    IncorrectJustifyOnHeader("Заголовок раздела должен быть выровнен по центру"),
    IncorrectJustifyOnPictureDescription("Подпись рисунка должна быть выровнена по центру"),
    IncorrectJustifyOnTableTitle("Заголовок таблицы должен быть выровнен по левому краю"),
    IncorrectJustifyOnText("Текст должен быть выровнен по ширине"),
    BackgroundColor("Найден цветной фон"),
    SpacingBefore("Найден интервал перед абзацем"),
    SpacingAfter("Найден интервал после абзаца"),
    IncorrectFont("Неверный шрифт: необходим Times New Roman"),
    IncorrectPictureDescriptionPattern("Неверный формат подписи рисунка"),
    TableWithoutTitle("Не найден заголовок таблицы/продолжение таблицы")
}