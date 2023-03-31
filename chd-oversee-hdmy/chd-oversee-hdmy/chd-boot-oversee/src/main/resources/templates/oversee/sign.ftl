<#assign base=springMacroRequestContext.getContextUrl("")>
<#setting classic_compatible=true>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <style>

        body{
            margin: 0px !important;
        }

        .page-div{
            width: 100vw;
            height: 100vh;
            overflow: hidden;

        }
        .container-div{
            width: 100%;
            height: 100%;
            padding: 5px;
            /*padding-bottom: 10px;*/
            display: flex;
            flex-direction: row;
            align-items: center;
            /*justify-content: space-between;*/
        }

        .signature-button{
            display: flex;
            flex-direction: column;
            height: 220px;
            /*width: 120px;*/
            /*padding-right: 10px;*/
            /*transform: rotateZ(90deg);*/
        }

        .signature-button button{
            width: 45px;
            transform: rotateZ(90deg);
        }

        .signature-div{
            margin-bottom: 10px;
            border:#000000 solid 2px;
            border-radius: 5px;
            /*transform: rotateZ(90deg);*/
            /*transform-origin: 35% 15%;*/
        }
    </style>
</head>

<body>
<div class="page-div">
    <div class="container-div">
        <div class="signature-button">
            <button onclick="clickSave()" style="margin-bottom: 30px;">保存</button>
            <button onclick="clearSig()" style="margin-bottom: 30px;">清除</button>
            <button onclick="addLineWidth()" style="margin-bottom: 30px;">加粗</button>
            <button onclick="reduceLineWidth()" style="margin-bottom: 30px;">减小</button>
        </div>
        <div class="signature-div">
            <div id="signature"></div>
        </div >

    </div>

</div >


</body>

<script src="${base}/res/js/jquery.js"></script>
<script src="${base}/res/js/jSignature.min.js"></script>


<script>

    var defaultLineWidth = 2;

    //初始化画布
    $(document).ready(function(){
        var width = document.documentElement.clientWidth - 30 - 45;
        var height = document.documentElement.clientHeight - 40;

        if((height/width)>2){
            height = width*2;
        }

        $("#signature").jSignature({height:height,width:width,color:"#000",lineWidth:defaultLineWidth});
    })
    //重置画布
    function clearSig(){
        $("#signature").jSignature("clear");
    }
    //将画布内容显示到IMG
    function importImg(){
        var sig=$("#signature");
        $("#img").attr("src","data:"+sig.jSignature('getData'));
    }
    //将画布内容显示到IMG
    function clickSave(){
        var sig=$("#signature");
        var signatureData = sig.jSignature('getData');
        rotateBase64Img(signatureData,270,saveSign)
    }

    function addLineWidth(){
        if(defaultLineWidth>=50){
            return;
        }
        defaultLineWidth = defaultLineWidth + 1;
        $("#signature").jSignature('updateSetting', "lineWidth", defaultLineWidth,true);
    }

    function reduceLineWidth(){
        if(defaultLineWidth<=1){
            return;
        }
        defaultLineWidth = defaultLineWidth - 1;
        $("#signature").jSignature('updateSetting', "lineWidth", defaultLineWidth,true);
    }

    function saveSign(signatureData){
        console.log("signatureData",signatureData)
        var paramsData = {"signatureData":signatureData,"userId":"${userId}","rotate":1};
        $.ajax({
            type: "post",
            contentType: "application/json; charset=utf-8",
            dataType: "json",//返回值类型
            url: "${base}/oversee/signature/scanSave",
            data:JSON.stringify(paramsData),
            success: function(response){
                if(response.success){
                    console.log(response);
                    alert("保存成功")
                }else{
                    $('#msgH1').html('服务调用异常!')
                }
            },
            error : function(xhr, ts, et) {
                $('#msgH1').html('服务调用失败!')
                // layer.msg('服务调用失败!', {icon: 2});
            }
        });
    }

    function rotateBase64Img(src, edg, callback) {

        var canvas = document.createElement("canvas");
        var ctx = canvas.getContext("2d");

        var imgW;//图片宽度
        var imgH;//图片高度
        var size;//canvas初始大小

        if (edg % 90 != 0) {
            console.error("旋转角度必须是90的倍数!");
            throw '旋转角度必须是90的倍数!';
        }
        (edg < 0) && (edg = (edg % 360) + 360)
        const quadrant = (edg / 90) % 4; //旋转象限
        const cutCoor = {sx: 0, sy: 0, ex: 0, ey: 0}; //裁剪坐标

        var image = new Image();
        image.crossOrigin = "anonymous"
        image.src = src;

        image.onload = function () {

            imgW = image.width;
            imgH = image.height;
            size = imgW > imgH ? imgW : imgH;

            canvas.width = size * 2;
            canvas.height = size * 2;
            switch (quadrant) {
                case 0:
                    cutCoor.sx = size;
                    cutCoor.sy = size;
                    cutCoor.ex = size + imgW;
                    cutCoor.ey = size + imgH;
                    break;
                case 1:
                    cutCoor.sx = size - imgH;
                    cutCoor.sy = size;
                    cutCoor.ex = size;
                    cutCoor.ey = size + imgW;
                    break;
                case 2:
                    cutCoor.sx = size - imgW;
                    cutCoor.sy = size - imgH;
                    cutCoor.ex = size;
                    cutCoor.ey = size;
                    break;
                case 3:
                    cutCoor.sx = size;
                    cutCoor.sy = size - imgW;
                    cutCoor.ex = size + imgH;
                    cutCoor.ey = size + imgW;
                    break;
            }


            ctx.translate(size, size);
            ctx.rotate(edg * Math.PI / 180);
            ctx.drawImage(image, 0, 0);

            var imgData = ctx.getImageData(cutCoor.sx, cutCoor.sy, cutCoor.ex, cutCoor.ey);
            if (quadrant % 2 == 0) {
                canvas.width = imgW;
                canvas.height = imgH;
            } else {
                canvas.width = imgH;
                canvas.height = imgW;
            }
            ctx.putImageData(imgData, 0, 0);
            callback(canvas.toDataURL())
        };
    }
</script>




</html>
