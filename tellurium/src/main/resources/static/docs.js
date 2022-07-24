document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.response-code').forEach(element => {
        let index = 0
        element.classList.forEach(x => {
            if (x.startsWith("code")) {
                return
            }
            index++
        })
        let clazz = element.classList[index]
        element.classList.remove(element.classList[index])
        element.classList.add("code" + clazz[4])
    })
})