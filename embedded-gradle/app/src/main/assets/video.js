var xwalk = {
    playingIndex: 0,
    listenedXWalkVideos: false,
    listenVideos: function() {
		var videos = xwalk.getVideos();
        for (var i = 0; i < videos.length; i++) {
            xwalk.listenedXWalkVideos = true;
            videos[i].oncanplay = function() {
                window.xwalkExoPlayer.printWithJavaScript("oncanplay");
            };
            videos[i].oncanplaythrough = function() {
                window.xwalkExoPlayer.printWithJavaScript("oncanplaythrough");
            };
            videos[i].ondurationchange = function() {
                window.xwalkExoPlayer.printWithJavaScript("ondurationchange");
            };
            videos[i].onended = function() {
                //Remove DIV
                xwalk.hideDiv(xwalk.getVideoIndex(this));
                window.xwalkExoPlayer.showReplayButtonFromJS();
                window.xwalkExoPlayer.printWithJavaScript("onended");
            };
            videos[i].onerror = function() {
                //Remove DIV
                xwalk.hideDiv(xwalk.getVideoIndex(this));
                window.xwalkExoPlayer.printWithJavaScript("onerror");
            };
    //        videos[i].onloadeddata = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onloadeddata");
    //        };
    //        videos[i].onloadedmetadata = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onloadedmetadata");
    //        };
    //        videos[i].onloadstart = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onloadstart");
    //        };
    //        videos[i].onpause = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onpause");
    //        };
            videos[i].onplay = function() {
                window.xwalkExoPlayer.printWithJavaScript("onplay");
            };
            videos[i].onplaying = function() {
                xwalk.playingIndex = xwalk.getVideoIndex(this);
                window.xwalkExoPlayer.printWithJavaScript("onplaying");
            };
    //        videos[i].onseeked = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onseeked");
    //        };
    //        videos[i].onseeking = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onseeking");
    //        };
    //        videos[i].onstalled = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onstalled");
    //        };
    //        videos[i].onsuspend = function() {
    //            window.xwalkExoPlayer.printWithJavaScript("onsuspend");
    //        };
            videos[i].onwaiting = function() {
                window.xwalkExoPlayer.printWithJavaScript("onwaiting");
                window.xwalkExoPlayer.onWaitingFromJS();
            };
        }
        window.addEventListener("resize", xwalk.resizeVideos);
    },
    getVideos: function() {
        var videos = document.getElementsByTagName("video");
        if (videos.length == 0) {
            var iframes = document.getElementsByTagName("iframe");
            for (var i = 0; i < iframes.length; i++) {
                var documentObj = iframes[i].contentWindow;
                videos = documentObj.document.getElementsByTagName("video");
                window.xwalkExoPlayer.printWithJavaScript("==== Can't find Videos, so find in iframe for PPTV " + videos.length);
                // BUGBUG: need to be refined
                if (videos.length > 0) break;
            }
        }
        return videos;
    },
    showDiv: function(index) {
		var videos = xwalk.getVideos();
        var rect = xwalk.getAbsoluteLocationEx(videos[index]);
        var obj = document.createElement("div");
        obj.id="xwalkVideo" + index;
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
        img.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEYAAABGCAYAAABxLuKEAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMDY3IDc5LjE1Nzc0NywgMjAxNS8wMy8zMC0yMzo0MDo0MiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QjY2RjhFQjAyMENGMTFFNkI3OEVDQzU3RUE3NDQ5MDAiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QjY2RjhFQjEyMENGMTFFNkI3OEVDQzU3RUE3NDQ5MDAiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpCNjZGOEVBRTIwQ0YxMUU2Qjc4RUNDNTdFQTc0NDkwMCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpCNjZGOEVBRjIwQ0YxMUU2Qjc4RUNDNTdFQTc0NDkwMCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pgyf4f4AAApESURBVHja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"
        img.style.position = "absolute"
        img.style.width = "70px"
        img.style.height = "70px"
        img.style.left = "50%"
        img.style.marginLeft = "-40px"
        img.style.top = "50%"
        img.style.marginTop = "-40px"
        obj.appendChild(img);
        document.body.appendChild(obj);
        img.onclick = function() {
    //            alert("click on video element");
            xwalk.playVideo();
            window.xwalkExoPlayer.enterFullscreen();
        };
    },
    hideDiv: function(index) {
        //Remove DIV
        var playerDiv = document.getElementById("xwalkVideo" + index);
        if (playerDiv != null) {
            // Hide original div
            document.body.removeChild(playerDiv);
        }
    },
    resizeVideos: function() {
		var videos = xwalk.getVideos();
        for (var i = 0; i < videos.length; i++) {
            var playerDiv = document.getElementById("xwalkVideo" + i);
            if (playerDiv != null) {
                document.body.removeChild(playerDiv);
                xwalk.showDiv(i);
            }
        }
    },
    getVideoIndex: function(video) {
		var videos = xwalk.getVideos();
        for (var i = 0; i < videos.length; i++) {
            if (videos[i] == video) {
                return i;
            }
        }
        return 0;
    },
    getAbsoluteLocationEx: function(element) {
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
        window.xwalkExoPlayer.printWithJavaScript("====getAbsoluteLocationEx " + offsetTop + " " + offsetLeft + " " + offsetWidth + " " + offsetHeight)
        return {absoluteTop:offsetTop, absoluteLeft:offsetLeft,
                offsetWidth:offsetWidth, offsetHeight:offsetHeight};
    },
    replayVideo: function() {
        window.xwalkExoPlayer.printWithJavaScript("replayVideo " + xwalk.playingIndex);
		var videos = xwalk.getVideos();
        var currentVideo = videos[xwalk.playingIndex];
        currentVideo.currentTime = 0;
        currentVideo.play();
    },
    playVideo: function() {
        window.xwalkExoPlayer.printWithJavaScript("playVideo " + xwalk.playingIndex);
		var videos = xwalk.getVideos();
        var currentVideo = videos[xwalk.playingIndex];
        currentVideo.play();
    },
    pauseVideo: function() {
        window.xwalkExoPlayer.printWithJavaScript("pauseVideo " + xwalk.playingIndex);
		var videos = xwalk.getVideos();
        var currentVideo = videos[xwalk.playingIndex];
        currentVideo.pause();
        // Show div to cover the video.
        if (currentVideo.ended == false) {
            xwalk.hideDiv(xwalk.playingIndex);
            xwalk.showDiv(xwalk.playingIndex);
        }
    }
};
xwalk.listenVideos();