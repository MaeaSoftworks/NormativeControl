function toggleUi() {
    document.querySelector(".main").classList.toggle("hidden");
}

const genRandomNumber = (min, max) => {
    return Math.random() * (max - min) + min;
};

const genStar = () => {
    const star = document.createElement("div");
    star.classList.add("star");
    let x = genRandomNumber(1, 100);
    let y = genRandomNumber(1, 100);
    const {style} = star;
    style.left = Math.floor(x) + "%";
    style.top = Math.floor(y) + "%";
    style.setProperty("--star-size", genRandomNumber(1, 6) + "px");
    style.setProperty("--twinkle-duration", Math.ceil(genRandomNumber(1, 5)) + "s");
    style.setProperty("--twinkle-delay", Math.ceil(genRandomNumber(1, 5)) + "s");
    return star;
};

document.addEventListener("DOMContentLoaded", function () {
    const $el = document.querySelector(".space");
    for (let index = 0; index < 52; index++) {
        $el.append(genStar());
    }
    const canvas = document.getElementById("canvas");
    // noinspection JSUnresolvedFunction
    new Starback(canvas, {
        type: 'line',
        quantity: 20,
        width: window.screen.availWidth,
        height: window.screen.availHeight,
        backgroundColor: '#00000000',
        randomOpacity: true,
        frequency: 500,
        speed: 30,
        slope: {
            x: 1,
            y: 4
        },
        starColor: '#fff'
    });
})