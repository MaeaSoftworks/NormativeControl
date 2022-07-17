package com.maeasoftworks.docx4nc.enums

/**
 * Список статусов, которые получает документ в процессе обработки.
 * У каждого документа может быть только 1 статус, который будет меняться в процессе работы с ним.
 *
 * @author prmncr
 */
enum class Status {


    /**
     * Файл готов к постановке в очередь
     *
     * @author prmncr
     */
    READY_TO_ENQUEUE,

    /**
     * Файл в очереди на обработку
     *
     * @author prmncr
     */
    QUEUE,

    /**
     * Файл обрабатывается
     *
     * @author prmncr
     */
    PROCESSING,

    /**
     * Во время обработки документа возникла ошибка
     *
     * @author prmncr
     */
    ERROR,

    /**
     * Обработка файла завершена, можно получать результат
     *
     * @author prmncr
     */
    READY,


    RENDER_ERROR,

    /**
     * Состояние файла неизвестно
     *
     * @author prmncr
     */
    UNDEFINED,

    /**
     * Обработка файла завершена, файл сохранён в базе данных, можно получать результат
     *
     * @author prmncr
     */
    SAVED
}
