<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
    <meta charset="utf-8">
    <title></title>
</head>
<body>
<h2>File Upload </h2>
<input type="file" name="file1" id="file_to_upload" accept="video/mp4">
<br/>
<input type="button" value="Upload File" onclick="uploadFile()">
<progress id="progressBar" value="0" max="100" style="width:300px;display:none"></progress>
<p id="loaded_n_total"></p>
</body>
<script>
    function uploadFile() {
        // get the file
        let file = document.getElementById("file_to_upload").files[0];

        //print file details
        console.log("File Name : ", file.name);
        console.log("File size : ", file.size);
        console.log("File type : ", file.type);

        // create form data to send via XHR request
        var formdata = new FormData();
        formdata.append(file.name, file);

        //create XHR object to send request
        var ajax = new XMLHttpRequest();

        ajax.onreadystatechange = function () {
            if (ajax.readyState === XMLHttpRequest.DONE) {
                alert(ajax.status);
                alert(ajax.responseText)
            }
        }


        // add progress event to find the progress of file upload
        ajax.upload.addEventListener("progress", progressHandler);


        // initializes a newly-created request
        ajax.open("POST", "/upload"); // replace with your file URL

        // send request to the server
        ajax.send(formdata);

    }

    function progressHandler(ev) {

        let totalSize = ev.total; // total size of the file in bytes
        let loadedSize = ev.loaded; // loaded size of the file in bytes

        document.getElementById("loaded_n_total").innerHTML = "Uploaded " + loadedSize + " bytes of " + totalSize + " bytes.";

        // calculate percentage
        var percent = (ev.loaded / ev.total) * 100;
        document.getElementById("progressBar").style.display = "";
        document.getElementById("progressBar").value = Math.round(percent);

    }
</script>
</html>