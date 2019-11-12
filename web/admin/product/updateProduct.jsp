<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>

    <style type="text/css">

        body {
            margin-left: 3px;
            margin-top: 0px;
            margin-right: 3px;
            margin-bottom: 0px;
        }

        .STYLE1 {
            color: #e1e2e3;
            font-size: 12px;
        }

        .STYLE6 {
            color: #000000;
            font-size: 12px;
        }

        .STYLE10 {
            color: #000000;
            font-size: 12px;
        }

        .STYLE19 {
            color: #344b50;
            font-size: 12px;
        }

        .STYLE21 {
            font-size: 12px;
            color: #3b6375;
        }

        .STYLE22 {
            font-size: 12px;
            color: #295568;
        }

        -->
    </style>
</head>


<body>
<%--<c:if test="${empty categories}">
    <jsp:forward page="/admin/productServlet?op=findAllCategories&jsp=updateProduct"></jsp:forward>
</c:if>--%>
<%-- <form method="post" action="${pageContext.request.contextPath }/ProductServlet"> --%>
<form method="post" action="${pageContext.request.contextPath }/admin/productServlet" enctype="multipart/form-data">

    <input type="hidden" name="op" value="updateProduct"/>
    <%--传参pid--%>
    <input type="hidden" name="id" value="${requestScope.product.id}">
    <%--如果图片不作修改，图片为空，则传参为原来的url--%>
    <%--    <input type="hidden" name="imgUrl" value="${requestScope.product.imgUrl}">
        ${requestScope.product.imgUrl}--%>

    <table width="100%" border="0" align="center" cellpadding="0"
           cellspacing="0">
        <tr>
            <td height="30">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td height="24" bgcolor="#353c44">
                            <table width="100%"
                                   border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td>
                                        <table width="100%" border="0" cellspacing="0"
                                               cellpadding="0">
                                            <tr>
                                                <td width="6%" height="19" valign="bottom">
                                                    <div
                                                            align="center">
                                                        <img src="images/tb.gif" width="14" height="14"/>
                                                    </div>
                                                </td>
                                                <td width="94%" valign="bottom"><span class="STYLE1">
														修改商品</span>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td>
                                        <div align="right">
											<span class="STYLE1"> 
												 
											</span>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <table width="100%" border="0" cellpadding="0" cellspacing="1" bgcolor="#a8c7ce">
                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">分类</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">
                                <select name="cid" id="st" onchange="change()">
                                    <c:forEach items="${applicationScope.categories}" var="category">
                                        <%--value定义送往服务器的选项值。--%>
                                        <%--如何回显上次的选择值？？———— 判断外键cid相等--%>
                                        <option value="${category.id}"
                                                <c:if test="${category.id eq requestScope.product.cid}">selected</c:if>>
                                                ${category.cname}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </td>

                    </tr>

                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">商品号</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <input type="text " name="productNum" value="${product.productNum}"/>

                            </div>
                        </td>

                    </tr>

                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">总库存</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <input type="text " name="totalStockCount" value="${product.totalStockCount }"/>

                            </div>
                        </td>

                    </tr>

                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">商品名称</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <input type="text " name="productName" width="200" value="${product.productName}"/>

                            </div>
                        </td>

                    </tr>


                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">商城价:￥</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <input type="text " name="webStorePrice" value="${product.webStorePrice }"/><br>

                            </div>
                        </td>

                    </tr>


                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">市场价:￥</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <input type="text " name="marketPrice" value="${product.marketPrice }"/><br>

                            </div>
                        </td>

                    </tr>

                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">图片</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <%----%>
                                如果需要修改图片，请点击“选择文件”
                                <input type="file" name="imgUrl" accept="image/*"/><br>

                                <img id="oldImg" width="200px" height="180px"
                                     src="${pageContext.request.contextPath}/${product.imgUrl}"/>

                            </div>
                        </td>

                    </tr>

                    <tr>
                        <td width="4%" height="20" bgcolor="d3eaef" class="STYLE10">
                            <div
                                    align="center">
                                <input type="checkbox" name="checkbox" id="checkbox11"/>
                            </div>
                        </td>
                        <td width="10%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="center">
                                <span class="STYLE10">商品描述</span>
                            </div>
                        </td>
                        <td width="80%" height="20" bgcolor="d3eaef" class="STYLE6">
                            <div
                                    align="left">

                                <textarea name="description" cols="80" rows="5">${product.description}</textarea>

                            </div>
                        </td>

                    </tr>

                </table>
            </td>
        </tr>

        <tr>
            <td height="30">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="33%">
                            <div align="left">

                        </td>
                        <td width="67%">
                            <div align="right">
                                <input type="submit" value="修改"/>
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>

</form>
<script>
    function setOldUrl() {
        document.getElementById('oldImg')
    }
</script>
</body>
</html>




