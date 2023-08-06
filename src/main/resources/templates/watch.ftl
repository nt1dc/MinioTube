<!DOCTYPE html>
<html>
<head>
    <link href="https://vjs.zencdn.net/7.15.4/video-js.css" rel="stylesheet">
    <script src="https://vjs.zencdn.net/7.15.4/video.min.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            var player = videojs("my-video");
        });
    </script>
</head>
<body>
<video
        id="my-video"
        class="video-js vjs-default-skin"
        controls
        preload="auto"
        width="640"
        height="360"
>
    <source src="/streams/${videoId}" type="video/mp4">
</video>
</body>
</html>
