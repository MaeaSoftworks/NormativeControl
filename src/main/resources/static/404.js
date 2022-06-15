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

document.addEventListener("DOMContentLoaded", function() {
    const $el = document.querySelector(".space");
    for (let index = 0; index < 52; index++) {
        $el.append(genStar());
    }
})