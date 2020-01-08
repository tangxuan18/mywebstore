<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/template.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>web store - Shopping Cart</title>
    <meta name="keywords"
          content="web store, shopping cart, free template, ecommerce, online shop, website templates, CSS, HTML"/>
    <meta name="description" content="web store, Shopping Cart, online store template "/>
    <link href="templatemo_style.css" rel="stylesheet" type="text/css"/>

    <link rel="stylesheet" type="text/css" href="css/ddsmoothmenu.css"/>

    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/ddsmoothmenu.js">


    </script>

    <script type="text/javascript">

        ddsmoothmenu.init({
            mainmenuid: "top_nav", //menu DIV id
            orientation: 'h', //Horizontal or vertical menu: Set to "h" or "v"
            classname: 'ddsmoothmenu', //class added to menu's outer DIV
            //customtheme: ["#1c5a80", "#18374a"],
            contentsource: "markup" //"markup" or ["container_id", "path_to_menu_file"]
        })
    </script>
</head>

<body>
<%--加载分类，由于--%>
<%--<c:if test="${empty categories }">
    <jsp:forward page="/mainServlet?op=findAllCategories&jsp=shoppingcart"></jsp:forward>
</c:if>--%>
<div id="templatemo_body_wrapper">
    <div id="templatemo_wrapper">
        <div id="content" class="float_r">
            <h4><img src="${pageContext.request.contextPath }/images/cart.gif"/>订单详情</h4>
            <form action="">
                <table width="680px" cellspacing="0" cellpadding="5" bgcolor="#f0f8ff">
                    <tr bgcolor="#ddd">
                        <th width="220" align="left">图片</th>
                        <th width="180" align="left">描述</th>
                        <th width="100" align="center">数量</th>
                        <th width="60" align="right">价格</th>
                        <th width="60" align="right">总价</th>
                        <th width="90"></th>

                    </tr>
                    <%--                        <c:if test="${!empty user }">--%>
                    <c:set var="totalCount" value="0"> </c:set>
                    <c:set var="sum" value="0"> </c:set>
                    <c:forEach items="${requestScope.orderItems}" var="item">
                        <tr>
                            <td><img src="${item.imgUrl }" style="width: 200px;height: 180px"
                                     alt=""/></td>
                            <td>${item.productName }</td>
                            <td align="center">
                                <font style="width: 20px; text-align: right">${item.productCount }</font>
                            </td>
                            <td align="right">${item.webStorePrice } </td>
                            <td align="right">${item.webStorePrice*item.productCount } </td>
                        </tr>
                        <c:set var="totalCount" value="${totalCount + item.productCount}"> </c:set>
                        <c:set var="sum" value="${sum + item.productCount*item.webStorePrice}"> </c:set>
                    </c:forEach>
                    <tr>
                        <td>总件数:<span class="sumPrice"> ${totalCount}</span>件</td>
                        <td>总金额:<span class="sumPrice"> ${sum}</span>元</td>
                    </tr>
                    <%--                        <h4>总件数:<span class="sumPrice"> ${totalCount}</span>件</h4>--%>
                    <%--                        <h4>总金额:<span class="sumPrice"> ${sum}</span>元</h4>--%>
                    <%--                        </c:if>--%>

                </table>
                <table width="680px" cellspacing="0" cellpadding="5">
                    <tr>
                        <td>
                            <img src="${pageContext.request.contextPath }/images/pay/支付宝支付.jpg" style="width: 200px;height: 300px" alt="支付宝支付"/>
                        </td>
                        <td>
                            <img src="${pageContext.request.contextPath }/images/pay/微信支付.jpg" style="width: 200px;height: 300px" alt="微信支付"/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
        <div style="float:right; width: 255px; margin-top: 20px;">
            <p><a href="${pageContext.request.contextPath}/orderServlet?op=findUserOrders">查看订单</a></p>
        </div>
        <div class="cleaner"></div>
    </div>

    <div id="templatemo_footer">
        Copyright (c) 2016 <a href="#">shoe商城</a> | <a href="#">版权所有</a>
    </div>

</div>

</body>
</html>