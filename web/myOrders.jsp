<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
<%--加载分类--%>
<%--<c:if test="${empty categories }">
    <jsp:forward page="/mainServlet?op=findAllCategories&jsp=myOrders"></jsp:forward>
</c:if>--%>
<div id="templatemo_body_wrapper">
    <div id="templatemo_wrapper">

        <div id="templatemo_header">
            <div id="site_title"><h1><a href="http://localhost/${pageContext.request.contextPath }">web store
                Store</a></h1></div>
            <div id="header_right">
                <p>
                    <c:if test="${!empty sessionScope.user }">
                        <a href="${pageContext.request.contextPath }/user/personal.jsp">我的个人中心</a> |
                        <a href="${pageContext.request.contextPath }/cartServlet?op=findCart&cartJsp=shoppingcart">购物车</a> |
                        <a href="${pageContext.request.contextPath }/orderServlet?op=findUserOrders">我的订单</a> |
                    </c:if>
                    <c:if test="${empty sessionScope.user }">
                    <a href="${pageContext.request.contextPath }/user/login.jsp">登录</a> |
                    <a href="${pageContext.request.contextPath }/user/regist.jsp">注册</a></p>
                </c:if>
                <c:if test="${!empty sessionScope.user }">
                    ${sessionScope.user.nickname }
                    <a href="${pageContext.request.contextPath }/userServlet?op=logout">退出</a></p>
                </c:if>
            </div>
            <div class="cleaner"></div>
        </div> <!-- END of templatemo_header -->

        <div id="templatemo_menubar">
            <div id="top_nav" class="ddsmoothmenu">
                <ul>
                    <li><a href="${pageContext.request.contextPath }/index.jsp" class="selected">主页</a></li>
                </ul>
                <br style="clear: left"/>
            </div> <!-- end of ddsmoothmenu -->
            <div id="templatemo_search">
                <form action="${pageContext.request.contextPath }/mainServlet" method="get">
                    <input type="hidden" name="op" value="findProductsByKeyword"/>
                    <input type="text" value="${keyword}" name="keyword" id="keyword" title="keyword"
                           onfocus="clearText(this)" onblur="clearText(this)" class="txt_field"/>
                    <input type="submit" name="Search" value=" " alt="Search" id="searchbutton" title="Search"
                           class="sub_btn"/>
                </form>
            </div>
        </div> <!-- END of templatemo_menubar -->
        <div class="copyrights">Collect from <a href="#" title="Web商城">Web商城</a></div>

        <div id="templatemo_main">
            <div id="sidebar" class="float_l">
                <div class="sidebar_box"><span class="bottom"></span>
                    <h3>品牌</h3>
                    <div class="content">
                        <ul class="sidebar_list">
                            <c:forEach items="${categories}" var="category" varStatus="vs">
                                <c:if test="${vs.index !=0}">
                                    <c:if test="${vs.index != fn:length(categories)-1 }">
                                        <li>
                                            <a href="${pageContext.request.contextPath }/mainServlet?op=findProductsByCid&cid=${category.id}&cname=${category.cname}">${category.cname}</a>
                                        </li>
                                    </c:if>
                                </c:if>
                                <c:if test="${vs.index==0 }">
                                    <li class="first">
                                        <a href="${pageContext.request.contextPath }/mainServlet?op=findProductsByCid&cid=${category.id}&cname=${category.cname}">${category.cname}</a>
                                    </li>
                                </c:if>
                                <c:if test="${vs.index == fn:length(categories)-1 }">
                                    <li class="last">
                                        <a href="${pageContext.request.contextPath }/mainServlet?op=findProductsByCid&cid=${category.id}&cname=${category.cname}">${category.cname}</a>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>

            <div id="content" class="float_r">

                <h4>我所有的订单</h4>
                <table border="1" width="700" style="border-style: inherit; border-color: black;">

                    <tr style="color: grey">
                        <td>订单号</td>
                        <td>下单时间</td>
                        <td>订单总金额</td>
                        <td>订单详情</td>
                        <td>订单状态</td>
                        <td>操作</td>
                    </tr>
                    <c:forEach items="${requestScope.orders }" var="order" varStatus="status">
                        <tr style="color: grey">
                                <%--                            <td>${status.count}</td>--%>
                            <td>${order.orderNum}</td>
                            <td>${order.orderTime}</td>
                            <td>${order.totalPrice }</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/orderServlet?op=userOrderDetail&orderNum=${order.orderNum}&toJsp=userOrderDetail">订单详情</a>
                            </td>
                            <td>
                                <c:if test="${order.payStatus == -1}">卖家取消了订单</c:if>
                                <c:if test="${order.payStatus == 0}">买家已取消订单</c:if>
                                <c:if test="${order.payStatus == 1}">买家已下单，等待买家付款</c:if>
                                <c:if test="${order.payStatus == 2}">买家已付款，准备发货</c:if>
                                <c:if test="${order.payStatus == 3}">卖家已发货</c:if>
                                <c:if test="${order.payStatus == 4}">买家已确认收货</c:if>
                                <c:if test="${order.payStatus == 5}">买家申请退款</c:if>
                                <c:if test="${order.payStatus == 6}">退款成功</c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.payStatus == 1}">
                                        <a href="javascript:if(confirm('确定取消订单吗?')) location='${pageContext.request.contextPath}/orderServlet?op=cancelOrder&oid=${order.orderId}'">取消订单</a>
                                        <%--                                    <form id="_cancelOrder" method="post" action="${pageContext.request.contextPath}/orderServlet" >
                                                                                <input type="hidden" name="op" value="cancelOrder"/>
                                                                                <input type="hidden" name="oid" value="${order.orderId}"/>
                                                                                <a href="javascript:if(confirm('确定取消订单吗?')) document.getElementById('_cancelOrder').submit();">取消订单</a>
                                                                            </form>--%>
                                    </c:when>
                                    <c:when test="${order.payStatus == 3}">
                                        <div>
                                            <a href="javascript:if(confirm('确认收货吗?')) location='${pageContext.request.contextPath}/orderServlet?op=confirmProductsReceipt&oid=${order.orderId}'">确认收货</a>
                                        </div>
                                        <div>
                                            <a href="javascript:if(confirm('确定申请退款吗?')) location='${pageContext.request.contextPath}/orderServlet?op=requestRefund&oid=${order.orderId}'">申请退款</a>
                                        </div>
                                    </c:when>
                                    <c:when test="${order.payStatus == 2 || order.payStatus == 4}">
                                        <a href="javascript:if(confirm('确定申请退款吗?')) location='${pageContext.request.contextPath}/orderServlet?op=requestRefund&oid=${order.orderId}'">申请退款</a>
                                    </c:when>
                                </c:choose>

                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <p><a href="${pageContext.request.contextPath}/index.jsp">继续购物</a></p>
            </div>
            <div class="cleaner"></div>
        </div>

        <div id="templatemo_footer">
            Copyright (c) 2016 <a href="#">Web商城</a> | <a href="#">商城后台</a>
        </div>

    </div>
</div>

</body>
</html>