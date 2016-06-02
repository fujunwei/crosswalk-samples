var videos = document.getElementsByTagName("video");
var i;
var divId = 1;
for (i = 0; i < videos.length; i++) {
	videos[i].onplaying = function() {
//		alert(this.duration);
		var rect = getAbsoluteLocationEx(this);
		var obj = document.createElement("div");
        obj.id="xwalkVideo" + divId;
        divId++;
        obj.style.width = rect.offsetWidth + "px"
        obj.style.height = rect.offsetHeight + "px"
        obj.style.position = "absolute"
        obj.style.zIndex = "999"
        obj.style.background = "black"
//        obj.filter = "alpha(Opacity=80);-moz-opacity:0.8;opacity: 0.8;z-index:20000; background-color:#ffffff";
        obj.style.top = rect.absoluteTop + "px"
        obj.style.left = rect.absoluteLeft + "px"
//        obj.innerText = "sdsdsdaf"
        obj.align = "center"
        var img = document.createElement("img");
        img.src = "data:image/png;base64,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";
        obj.appendChild(img);
        document.body.appendChild(obj);
        img.onclick = function() {
//            alert("click on video element");
            window.xwalkExoPlayer.enterFullscreen();
        };
	};
}
function getAbsoluteLocationEx(element) {
    if(element==null) {
        return null;
    }
    var elmt = element;
    var offsetTop = elmt.offsetTop;
    var offsetLeft = elmt.offsetLeft;
    var offsetWidth = elmt.offsetWidth;
    var offsetHeight = elmt.offsetHeight;
    while (elmt = elmt.offsetParent) {
        // add this judge
        if(elmt.style.position == 'absolute'|| elmt.style.position == 'relative'
                ||(elmt.style.overflow != 'visible' && elmt.style.overflow != '')) {
            break;
        }
        offsetTop += elmt.offsetTop;
        offsetLeft += elmt.offsetLeft;
    }
    return {absoluteTop:offsetTop, absoluteLeft:offsetLeft,
            offsetWidth:offsetWidth, offsetHeight:offsetHeight};
}
