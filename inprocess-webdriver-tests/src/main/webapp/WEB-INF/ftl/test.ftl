<#-- @ftlvariable name="contact" type="com.thoughtworks.webdriver.inprocess.testapp.Contact" -->
<html>
<head>
    <title>Test Application</title>
</head>
<body>
<h3>
    Contact Details
</h3>

<form action="" method="POST">
    <label>
        <input type="text" value="${contact.getName()!}"/>
    </label>
</form>
</body>
</html>