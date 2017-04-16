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
    $function = $_POST["function"];

    if (isset($function)) {
        logs(date("Y-m-d h:i:s") . "\t\t" . json_encode($_POST) . "\n");

        /******************** #user ********************/
        if ($function == "check_user") {
            $username = $_POST["username"];
            $password = $_POST["password"];

            echo json_encode(sql("SELECT id FROM officer WHERE username = '$username' AND password = '$password'", false));
        }
        /******************** #accident ********************/
        else if ($function == "get_accidents") {
            $status = $_POST["status"];

            echo json_encode(sql("SELECT a.type_id, at.title type, a.title, a.people_id, p.name people, a.officer_id, (SELECT o.name FROM officer o WHERE o.id = a.officer_id) officer, a.photo, a.detail, a.location_x, a.location_y, a.status, a.date, a.approve_date
                FROM accident a, accident_type at, people p
                WHERE a.type_id = at.id AND a.people_id = p.id AND a.status = $status"));
        }
    }
?>
