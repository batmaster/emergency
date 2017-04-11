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
            return $rows;
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

    $function = $_POST["function"];

    if (isset($function)) {
        logs(date("Y-m-d h:i:s") . "\t\t" . json_encode($_POST) . "\n");

        /******************** #user ********************/
        if ($function == "check_user") {
            $username = $_POST["username"];
            $password = $_POST["password"];

            echo json_encode(sql("SELECT COUNT(*) count FROM user WHERE username = '$username'", false));
        }
        else if ($function == "add_user") {
            $name = $_POST["name"];
            $lastname = $_POST["lastname"];
            $username = $_POST["username"];
            $password = $_POST["password"];
            $email = $_POST["email"];
            $level = $_POST["level"];
            $group = $_POST["group"];
            $phone = $_POST["phone"];
            $birthday = $_POST["birthday"];

            if ($level == 0) {
                sql("INSERT INTO user (username, password, name, lastname, email, level, phone, birthday, register_date)
                VALUES ('$username', '$password', '$name', '$lastname', '$email', $level, '$phone', '$birthday', NOW())");
            }
            else {
                $code = "gl" . (intval(sql("SELECT id FROM `group` ORDER BY id DESC LIMIT 1", false)[id]) + 1);
                sql("INSERT INTO `group` (title, code) VALUES ('$group', '$code')");

                $group_id = sql("SELECT id FROM `group` WHERE code = '$code'", false)[id];

                sql("INSERT INTO user (username, password, name, lastname, email, level, phone, birthday, register_date, group_id)
                VALUES ('$username', '$password', '$name', '$lastname', '$email', $level, '$phone', '$birthday', NOW(), $group_id)");
            }

            echo json_encode(sql("SELECT id, group_id FROM user WHERE username = '$username' AND password = '$password'", false));
        }
        else if ($function == "login") {
            $username = $_POST["username"];
            $password = $_POST["password"];

            echo json_encode(sql("SELECT id, level, group_id FROM user WHERE (username = '$username' OR email = '$username') AND password = '$password'", false));
        }
?>
