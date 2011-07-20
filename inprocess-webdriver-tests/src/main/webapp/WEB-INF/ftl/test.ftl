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
        <input name="contactName" type="text" value="${contact.getName()!}"/>
    </label>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>