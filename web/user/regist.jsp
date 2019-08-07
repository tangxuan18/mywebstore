<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link href="${pageContext.request.contextPath }/user/css/style.css" rel='stylesheet' type='text/css'/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!--webfonts-->
    <!--//webfonts-->
    <script src="js/setDate.js" type="text/javascript"></script>
    <script>
        /*        // 校验用户名
                function checkUsername() {
                    // 获取用户名的值
                    var username = $("#username").val();
                    // 定义正则
                    var reg_username = /^\w{8, 20}$/;
                    // 判断，给出提示信息
                    var flag = reg_username.test(username);
                    if(flag){
                        $("#username").css("border", "");
                    }else {
                        alert("不合法");
                        $("#username").css("border", "1px solid red");
                    }
                    return flag;
                }

                $(function () {
                    // 当表单提交时，调用所有的校验方法
                    $("#registForm").submit(function () { // 调用submit()
                        return checkUsername() && checkPassword(); // &连接多个方法
                    });

                    // 当某一个组件blur时，调用对应的校验方法
                    $("#username").blur(checkUsername);
                });*/

        function checkUsername() {
            // 获取用户名的值
            var usernameElement = document.getElementById("username");
            var usernameSpan = document.getElementById("usernameSpan");
            var username = usernameElement.value;
            // 定义正则
            var reg_username = /^\w{2,20}$/;
            // var reg_username =/^[a-z][a-z0-9]{5}$/i; // //用户名的规则：第一位是字母，只有由数字与字母组成，6位。
            // 判断，给出提示信息
            var flag = reg_username.test(username);
            if (flag) {
                usernameSpan.innerHTML = "格式正确".fontcolor("green");
            } else {
                usernameSpan.innerHTML = "2-20位字符".fontcolor("red");
            }
            checkUsernameByAjax();
            return flag;
        }

        function checkPassword() {
            var password = document.getElementById("password").value;
            var passwordSpan = document.getElementById("passwordSpan");
            var reg_password = /^\w{3,10}$/;
            var flag = reg_password.test(password);
            if (flag) {
                passwordSpan.innerHTML = "正确".fontcolor("green");
            } else {
                passwordSpan.innerHTML = "请输入3-10位".fontcolor("red");
            }
            return flag;
        }

        function checkConfirmPassword() {
            var password = document.getElementById("password").value; //第一次输入的密码
            var confirmPassword = document.getElementById("confirmPassword").value;
            if (confirmPassword.length > 0) {
                var confirmPasswordSpan = document.getElementById("confirmPasswordSpan");
                if (password.valueOf() == confirmPassword.valueOf()) {
                    confirmPasswordSpan.innerHTML = "正确".fontcolor("green");
                    return true;
                } else {
                    confirmPasswordSpan.innerHTML = "两次输入密码不同".fontcolor("red");
                    return false;
                }
            }
        }

        function checkNickname() {
            var nickname = document.getElementById("nickname").value;
            var nicknameSpan = document.getElementById("nicknameSpan");
            var reg_nickname = /^[\u4E00-\u9FA5A-Za-z0-9]{2,20}$/;
            var flag = reg_nickname.test(nickname);
            if (flag) {
                nicknameSpan.innerHTML = "正确".fontcolor("green");
            } else {
                nicknameSpan.innerHTML = "1-20位中文、英文、数字但不包括下划线等符号".fontcolor("red");
            }
            return flag;
        }

        function checkEmail() {
            var email = document.getElementById("email").value;
            var emailSpan = document.getElementById("emailSpan");
            var reg_email = /^[a-z0-9]\w+@[a-z0-9]{2,3}(\.[a-z]{2,3}){1,2}$/i;
            var flag = reg_email.test(email);
            if (flag) {
                emailSpan.innerHTML = "正确".fontcolor("green");
            } else {
                emailSpan.innerHTML = "非法邮箱格式".fontcolor("red");
            }
            return flag;
        }

        function checkBirthday() {
            var birthday = document.getElementById("birthday").value;
            var span = document.getElementById("birthdaySpan");
            var pattern = /^(19|20)\d{2}\-((0?[1-9])|(1[0-2]))\-((0?[1-9])|([1-2]\d)|3[01])$/;
            var flag = pattern.test(birthday);
            if(flag) {
                var date = new Date(birthday);
                if(date < new Date("1919-12-31") || date > new Date()) {
                    return false;
                }
                var month = birthday.substring(birthday.indexOf("-")+1, birthday.lastIndexOf("-"));
                span.innerHTML = "正确".fontcolor("green");
                return date && (date.getMonth()+1 == parseInt(month));
            }else {
                span.innerHTML = "生日格式不正确，格式如：yyyy-MM-dd".fontcolor("red");
            }
            return flag;
        }


        function checkForm() {
            return checkUsername() && checkPassword() && checkConfirmPassword() && checkNickname() && checkEmail() && checkBirthday();
        }

        function checkUsernameByAjax() {
            var username = document.getElementById("username").value;
            /*创建核心对象*/
            var xmlHttpRequest;
            if (window.XMLHttpRequest) {
                xmlHttpRequest = new XMLHttpRequest(); // code for chrome...
            } else {
                xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP"); // code for IE6, IE5
            }
            /*建立连接*/
            // get方式
            <%--xmlHttpRequest.open("get", "${pageContext.request.contextPath}/ajaxServlet?username=" + username, true); // get方式--%>
            // post方式
            xmlHttpRequest.open("post", "${pageContext.request.contextPath}/ajaxServlet", true);
            xmlHttpRequest.setRequestHeader("content-type", "application/x-www-form-urlencoded");
            /*发送请求*/
            // get方式
            // xmlHttpRequest.send();
            // post方式
            xmlHttpRequest.send("username=" + username);
            /*注册状态监控回调函数*/
            xmlHttpRequest.onreadystatechange = function (ev) {
                // 判断readyState就绪状态是否为4， 且status响应状态码是否为200
                if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
                    // 获取服务器的响应结果
                    var responseText = xmlHttpRequest.responseText;
                    var span = document.getElementById("usernameExistedSpan");
                    if (responseText == "false") {
                        span.style = "display: block; color: red";
                        span.innerText = "当前用户名已存在";
                    } else {
                        span.style = "display: block; color: green";
                        span.innerText = "当前用户名可用"
                    }
                }
            }
        }

    </script>
</head>

<body>
<div class="main" align="center">
    <div class="header">
        <h1>创建一个付费的新帐户！</h1>
    </div>
    <p></p>
    <form id="registForm" method="post" action="${pageContext.request.contextPath }/userServlet"
          onsubmit="return checkForm()">
        <input type="hidden" name="op" value="regist"/>
        <input type="hidden" name="registTime" value="">
        <ul class="left-form">
            <li>
                ${msg.error.username }<br/>
                <span id="usernameSpan"></span>
                <span id="usernameExistedSpan"></span>
                用户名：<input id="username" type="text" name="username" placeholder="用户名" value="${msg.username}"
                           required="required" onblur="checkUsername()"/>
                <a href="#" class="icon ticker"> </a>
                <div class="clear"></div>
            </li>
            <li>
                ${msg.error.password }<br/>
                <span id="passwordSpan"></span><br>
                密码：<input id="password" type="password" name="password" placeholder="密码" value="${msg.password}"
                          required="required" onblur="checkPassword()"/>
                <a href="#" class="icon into"> </a>
                <div class="clear"></div>
            </li>
            <li>
                <span id="confirmPasswordSpan"></span><br>
                确认密码：<input id="confirmPassword" type="password" name="password" placeholder="确认密码"
                            required="required" onblur="checkConfirmPassword()"/>
                <a href="#" class="icon into"> </a>
                <div class="clear"></div>
            </li>
            <li>
                ${msg.error.nickname }<br/>
                <span id="nicknameSpan"></span><br>
                昵称：<input id="nickname" type="text" name="nickname" placeholder="昵称" value="${msg.nickname}"
                          required="required" onblur="checkNickname()"/>
                <a href="#" class="icon ticker"> </a>
                <div class="clear"></div>
            </li>
            <li>
                ${msg.error.email }<br/>
                <span id="emailSpan"></span><br>
                绑定邮箱：<input id="email" type="text" name="email" placeholder="邮箱" value="${msg.email}"
                            required="required" onblur="checkEmail()"/>
                <a href="#" class="icon ticker"> </a>
                <div class="clear"></div>
            </li>

            <li>
                ${msg.error.birthday }<br/>
                    <span id="birthdaySpan"></span><br>
                生日：<input id="birthday" type="text" placeholder="出生日期" name="birthday" value="${msg.birthday}"
                          size="15" onblur="checkBirthday()"/>
                <div class="clear"></div>
            </li>
            <li>
                <input type="submit" value="创建账户">
                <div class="clear"></div>
            </li>
        </ul>

        <div class="clear"></div>

    </form>

</div>
<!-----start-copyright---->

<!-----//end-copyright---->

</body>

</html>