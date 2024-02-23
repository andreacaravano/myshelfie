<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">

<head>
    <title>My Shelfie Server - Logging Service</title>
    <style type="text/css">
        H1 {
            font-family: Tahoma, Arial, sans-serif;
            color: white;
            background-color: #525D76;
            font-size: 22px;
        }

        H2 {
            font-family: Tahoma, Arial, sans-serif;
            color: white;
            background-color: #525D76;
            font-size: 16px;
        }

        H3 {
            font-family: Tahoma, Arial, sans-serif;
            color: white;
            background-color: #525D76;
            font-size: 14px;
        }

        BODY {
            font-family: Tahoma, Arial, sans-serif;
            color: black;
            background-color: white;
        }

        B {
            font-family: Tahoma, Arial, sans-serif;
            color: white;
            background-color: #525D76;
        }

        P {
            font-family: Tahoma, Arial, sans-serif;
            background: white;
            color: black;
            font-size: 12px;
        }

        A {
            color: black;
        }

        HR {
            color: #525D76;
        }
    </style>
</head>

<body>
    <h1>My Shelfie Server - Logging Service</h1>
    <hr />

    <?php
    $entries = [];
    if ($handle = opendir('.')) {
        while (false !== ($entry = readdir($handle))) {
            if ($entry != "." && $entry != ".." && !preg_match("/index.php/", $entry) && !preg_match("/lck/", $entry)) {
                array_push($entries, $entry);
            }
        }
        closedir($handle);
    }
    sort($entries);
    if (count($entries) != 0) {
        echo "<ul>";
    } else echo "<p>No logs found.</p>";
    foreach ($entries as &$entry) {
        echo sprintf("<li><a href='%s'>%s</a></li>", $entry, $entry);
    }
    if (count($entries) != 0) {
        echo "</ul>";
    }
    ?>

    <hr />
    <h3>My Shelfie Server</h3>
</body>

</html>