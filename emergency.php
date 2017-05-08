<?php
    mysql_connect("localhost", "root", "rootaemysql") or die("ไม่สามารถเชื่อมต่อฐานข้อมูลได้");
    mysql_select_db("foundation") or die(mysql_error());
    mysql_query("SET NAMES utf8");

    header('Access-Control-Allow-Origin: *');

    function sql($sql, $asArray = true) {
        logs("\t" . $sql);

        $result = mysql_query($sql) or die($sql . "\n" . mysql_error());

        if ($result != undefined) {
            $rows = array();
            while ($r = mysql_fetch_assoc($result)) {
                // $rows[] = array_merge(array("i" => $number++), $r);
                $rows[] = $r;
            }

            if (count($rows) == 1 && !$asArray) {
                logs("\t\t\t". json_encode($rows[0]));
                return $rows[0];
            }

            logs("\t\t\t". json_encode($rows));
            return ["array" => $rows];
        }
    }

    function logs($str) {
        $fileName = "logs.txt";
        if (!file_exists($fileName)) {
            echo "Cannot find file.";
        } else {
            $fileHandle = fopen($fileName, "a") or die("Unable to open");
            fwrite($fileHandle, "\n" . $str);
            fclose($fileHandle);
        }
    }

    if (isset($_GET["function"])) {
        $g = $_GET["function"];
        if ($g == "clear") {
            $fileHandle = fopen("logs.txt", "r+") or die("Unable to open");
            ftruncate($fileHandle, 0);
            fclose($fileHandle);
            echo "clear ok";
        }
        else {
            echo "server ok";
        }
    }

    //
    //
    //
    logs(date("Y-m-d h:i:s") . "\t\t" . json_encode($_POST) . "\t>>\t" . json_encode($_FILES) .  "\n");

    $function = $_POST["function"];

    if (isset($function) || isset($_FILES["filUpload"])) {

        /******************** #user ********************/
        if ($function == "check_user") {
            $username = $_POST["username"];
            $password = $_POST["password"];

            echo json_encode(sql("SELECT id FROM officer WHERE username = '$username' AND password = '$password'", false));
        }
        /******************** #accident type ********************/
        else if ($function == "get_accident_types") {

            echo json_encode(sql("SELECT at.id, at.title, at.color, (SELECT COUNT(*) FROM accident a WHERE a.type_id = at.id) amount FROM accident_type at"));
        }
        /******************** #accident ********************/
        else if ($function == "get_accidents") {
            $status = $_POST["status"];
            $search = $_POST["search"];
            $phone = $_POST["phone"];

            $url = 'http://'. $_SERVER['SERVER_NAME'] . ":" . $_SERVER['SERVER_PORT'] . "/";

            echo json_encode(sql("SELECT a.id, a.type_id, at.title type, a.title, a.phone, a.location_x, a.location_y, a.status, a.date, at.color, CONCAT('$url', at.image) type_image
                FROM accident a, accident_type at
                WHERE a.type_id = at.id AND a.status = $status AND a.phone LIKE '%$phone%'
                AND (at.title LIKE '%$search%' OR a.title LIKE '%$search%' OR a.phone LIKE '%$search%')
                ORDER BY a.date DESC"));
        }
        else if ($function == "get_accident") {
            $aid = $_POST["aid"];

            echo json_encode(sql("SELECT a.id, a.type_id, at.title type, a.title, a.phone, a.location_x, a.location_y, a.status, a.date, at.color
                FROM accident a, accident_type at
                WHERE a.type_id = at.id AND a.id = $aid"));
        }
        else if ($function == "remove_accident") {
            $aid = $_POST["aid"];

            sql("DELETE FROM accident WHERE id = $aid");
            sql("DELETE FROM accident_image WHERE accident_id = $aid");

            echo json_encode(["ok" => 0]);
        }
        else if ($function == "get_images") {
            $aid = $_POST["aid"];

            $url = 'http://'. $_SERVER['SERVER_NAME'] . ":" . $_SERVER['SERVER_PORT'] . "/";

            echo json_encode(sql("SELECT id, CONCAT('$url', image) image FROM accident_image WHERE accident_id = $aid"));
        }
        else if ($function == "remove_image") {
            $id = $_POST["id"];

            sql("DELETE FROM accident_image WHERE id = $id");

            echo json_encode(["ok" => 0]);
        }
        else if ($function == "add_accident") {
            $title = $_POST["title"];
            $type_id = $_POST["type_id"];
            $location_x = $_POST["location_x"];
            $location_y = $_POST["location_y"];
            $phone = $_POST["phone"];

            sql("INSERT INTO accident (type_id, title, phone, location_x, location_y, date) VALUES ($type_id, '$title', '$phone', '$location_x', '$location_y', NOW())");

            echo json_encode(sql("SELECT id FROM accident WHERE phone = '$phone' AND title = '$title' AND location_x = '$location_x' AND location_y = '$location_y' ORDER BY id DESC LIMIT 1", false));

        }
        else if ($function == "edit_accident") {
            $aid = $_POST["aid"];
            $title = $_POST["title"];
            $officer_id = $_POST["officer_id"];
            $type_id = $_POST["type_id"];
            $location_x = $_POST["location_x"];
            $location_y = $_POST["location_y"];
            $status = $_POST["status"];

            sql("UPDATE accident SET title = '$title', officer_id = '$officer_id', type_id = $type_id, status = $status, location_x = '$location_x', location_y = '$location_y' WHERE id = $aid");

            echo json_encode(sql("SELECT id FROM accident WHERE title = '$title' AND location_x = '$location_x' AND location_y = '$location_y' ORDER BY id DESC LIMIT 1", false));

        }
        else if ($function == "summary_accidents") {
            $date = $_POST["date"];

            echo json_encode(sql("
                SELECT DATE_FORMAT(a.date, '%d') date, a.type_id, COUNT(*) amount, at.title, at.color
                FROM accident a, accident_type at WHERE MONTH(a.date) = MONTH('$date') AND YEAR(a.date) = YEAR('$date') AND a.type_id = at.id
                GROUP BY DATE(a.date), a.type_id
                ORDER BY DATE(a.date), a.type_id
            "));

        }
        /******************** #files ********************/
        else if (isset($_FILES["filUpload"])) {
            $uploaddir = 'uploads/';
            $uploadfile = $uploaddir . basename($_FILES['filUpload']['name']);

            logs(json_encode($uploadfile));

            if (move_uploaded_file($_FILES['filUpload']['tmp_name'], $uploadfile)) {
                $accident_id = explode("_", basename($_FILES['filUpload']['name']))[0];

                sql("INSERT INTO accident_image (accident_id, image) VALUES ($accident_id, '$uploadfile')");

                logs(json_encode("ok"));
            } else {
                logs(json_encode("nook"));
            }

        }
    }

    if (isset($_GET["id"])) {
        $aid = $_GET["id"];

        $detail = sql("SELECT a.id, a.type_id, at.title type, a.title, a.location_x, a.location_y, a.status, a.date, at.color
            FROM accident a, accident_type at
            WHERE a.type_id = at.id AND a.id = $aid", false);

        $url = 'http://'. $_SERVER['SERVER_NAME'] . ":" . $_SERVER['SERVER_PORT'] . "/";

        $images = sql("SELECT CONCAT('$url', image) image FROM accident_image WHERE accident_id = $aid")["array"];

        ?>

        <html lang="en">
        <head>
            <!-- Latest compiled and minified CSS -->
            <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

            <!-- Optional theme -->
            <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

            <!-- Latest compiled and minified JavaScript -->
            <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>

            <meta charset="UTF-8">
            <title><?php echo $detail["type"] . " - " . $detail["title"];?></title>
        </head>
        <body>
            <div class="container">
                <h1><?php echo $detail["title"];?></h1>
                <h3><?php echo $detail["type"];?></h3>
                <br>
                <h4>รับแจ้งเหตุ: <?php echo $detail["date"];?></h4>

                <?php for ($i = 0; $i < count($images); $i++) {?>
                    <a href="<?php echo $images[$i]["image"]; ?>"><img src="<?php echo $images[$i]["image"]; ?>"class="img-thumbnail" style="height: 350px; margin 4px"></a>
                <?php } ?>
            </div>
        </body>
        </html>

        <?php
    }


    // logs(json_encode($_FILES));

    // if (isset($_FILES)) {
    //
    //
    //
        // $uploaddir = 'uploads/';
        // $uploadfile = $uploaddir . basename($_FILES['uploaded_file']['name']);
        // if (move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $uploadfile)) {
        //     // echo json_encode("ok");
        // } else {
        //     // echo json_encode("nook");
        // }
    // }
?>
