<%--
  Created by IntelliJ IDEA.
  User: GFS
  Date: 2019-7-18
  Time: 08:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>top</title>
    <meta name="keywords" content="shoes store, free template, ecommerce, online shop, website templates, CSS, HTML"/>
    <meta name="description" content="Shoes Store is a free ecommerce template provided "/>
    <link href="templatemo_style.css" rel="stylesheet" type="text/css"/>

    <link rel="stylesheet" href="nivo-slider.css" type="text/css" media="screen"/>

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
<div id="templatemo_body_wrapper">
    <div id="templatemo_wrapper">

        <div id="templatemo_header">
            <div id="site_title"><h1><a href="http://localhost/${pageContext.request.contextPath }">Online Shoes
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
        </div>

    </div>
</div>
</body>
</html>
