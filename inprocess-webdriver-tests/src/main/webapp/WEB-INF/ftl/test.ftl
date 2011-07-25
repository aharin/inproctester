<#-- @ftlvariable name="message" type="java.lang.String" -->
<#-- @ftlvariable name="contact" type="com.thoughtworks.inprocess.testapp.Contact" -->
<html>
<head>
    <title>Test Application</title>
</head>
<body>
<h3>
    Contact Details
</h3>
<#if message??>
<div class="message">${message}</div>
</#if>
<form action="" method="POST">
    <label>
        <input name="contactName" type="text" value="${contact.getName()!}"/>
    </label>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>