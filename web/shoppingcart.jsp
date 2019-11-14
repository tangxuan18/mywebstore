<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/template.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Shoes Store - Shopping Cart</title>
    <meta name="keywords"
          content="shoes store, shopping cart, free template, ecommerce, online shop, website templates, CSS, HTML"/>
    <meta name="description" content="Shoes Store, Shopping Cart, online store template "/>
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
            <h4><img src="${pageContext.request.contextPath }/images/cart.gif"/>购物车</h4>
            <form action="">
                <table width="680px" cellspacing="0" cellpadding="5">
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
                    <c:forEach items="${cart.cartItems}" var="item">
                        <tr>
                            <td><img src="${item.product.imgUrl }" style="width: 200px;height: 180px"
                                     alt=""/></td>
                            <td>${item.product.productName }</td>
                            <td align="center">
                                <c:if test="${item.productCount != 1}">
                                    <a href="${pageContext.request.contextPath }/cartServlet?op=updateOneProductCount&uid=${user.uid}&itemId=${item.cartItemId}&addOrDelete=delete">
                                        <b> ➖ </b>
                                    </a>
                                </c:if>
                                <c:if test="${item.productCount == 1}">
                                    <a href="javascript:if(confirm('确实要从购物车删除吗?')) location='${pageContext.request.contextPath }/cartServlet?op=updateOneProductCount&uid=${user.uid}&itemId=${item.cartItemId}&addOrDelete=delete'">
                                        <b> ➖ </b>
                                    </a>
                                </c:if>
                                <font style="width: 20px; text-align: right"/>${item.productCount }</font>
                                <a href="${pageContext.request.contextPath }/cartServlet?op=updateOneProductCount&uid=${user.uid}&itemId=${item.cartItemId}&addOrDelete=add">
                                    <b> ➕ </b>
                                </a>
                            </td>
                            <td align="right">${item.product.webStorePrice } </td>
                            <td align="right">${item.product.webStorePrice*item.productCount } </td>
                        </tr>
                        <c:set var="totalCount" value="${totalCount + item.productCount}"> </c:set>
                        <c:set var="sum" value="${sum + item.productCount*item.product.webStorePrice}"> </c:set>
                    </c:forEach>
                    <tr>
                        <td>总件数:<span class="sumPrice"> ${totalCount}</span>件</td>
                        <td>总金额:<span class="sumPrice"> ${sum}</span>元</td>
                    </tr>
                    <%--                        <h4>总件数:<span class="sumPrice"> ${totalCount}</span>件</h4>--%>
                    <%--                        <h4>总金额:<span class="sumPrice"> ${sum}</span>元</h4>--%>
                    <%--                        </c:if>--%>
                </table>
            </form>
            <div style="float:right; width: 255px; margin-top: 20px;">
                <c:if test="${!empty cart.cartItems}">
                    <%--                    <p><a href="${pageContext.request.contextPath }/cartServlet?op=findCart&jsp=placeOrder">立即购买</a></p>--%>
                    <p><a href="${pageContext.request.contextPath }/placeOrder.jsp">立即购买</a></p>
                </c:if>
                <p><a href="${pageContext.request.contextPath}/index.jsp">继续购物</a></p>

            </div>
        </div>
        <div class="cleaner"></div>
    </div>

    <div id="templatemo_footer">
        Copyright (c) 2016 <a href="#">shoe商城</a> | <a href="#">版权所有</a>
    </div>

</div>

</body>
</html>