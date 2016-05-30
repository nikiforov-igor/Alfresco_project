<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <style type="text/css">
        #notification {
            width: 100%;
        }

        table {
            border: 1px solid #000;
            border-collapse: collapse;
            margin: 10px 5%;
            vertical-align: middle;
            width: 90%;
        }

        td {
            border: 1px solid #000;
            padding: 5px;
        }

        td.image-cell {
            width: 48px;
        }

        td.text-cell {
            text-align: justify;
        }
    </style>
</head>
<body>
<div id="notification">
    <table>
        <tr>
            <td class="image-cell"><img src="${image("defaultEmailNotificationTemplateImage.png")}"/></td>
            <td class="text-cell">${notificationText}</td>
        </tr>
    </table>
</div>
</body>
</html>
