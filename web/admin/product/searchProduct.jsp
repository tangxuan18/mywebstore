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

    </style>

</head>

<body>
<c:if test="${empty categories}">
    <%--如果跳转到categoryServlet，则categoryServlet只能转发到一个页面，不能用于多个页面--%>
    <%--    <jsp:forward page="/admin/categoryServlet?operation=findAllCategory"></jsp:forward>--%>
    <%--方法一：先查询数据库获得目录数据
         方法二：从context域获取目录数据--%>
    <jsp:forward page="/admin/productServlet?op=findAllCategories&jsp=searchProduct"></jsp:forward>
</c:if>

<form method="get" action="${pageContext.request.contextPath }/admin/productServlet?">

    <input type="hidden" name="op" value="searchPageProducts"/>
    <input type="hidden" name="num" value="1">

    <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr>
            <td height="30" colspan="4">
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
														搜索商品</span>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td>
                                        <div align="right">
											<span class="STYLE1"> 
												<!-- <input type="button" value="添加"/> -->
                                                <!-- <input type="submit" value="删除" /> -->
												&nbsp;&nbsp;
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


        <tr class="STYLE10">
            <td height="22" align="center" bgColor="#a8c7ce">
                商品编号：
            </td>
            <td class="ta_01" bgcolor="#a8c7ce">
                <input type="text" name="productNum" size="15" value="" id="Form1_userName"/>
            </td>
            <td height="22" align="center" bgcolor="#a8c7ce">
                分类：
            </td>
            <td bgcolor="#a8c7ce">

                <select name="cid" id="st" onchange="change()">
                    <option value="0" selected="selected">——请选择——</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}">${category.cname}</option>
                    </c:forEach>
                </select>


            </td>
        </tr>

        <tr class="STYLE10">
            <td height="22" align="center" bgcolor="#a8c7ce">
                商品名称：
            </td>
            <td bgcolor="#a8c7ce">
                <input type="text" name="productName" size="15" value="" id="Form1_userName" class="bg"/>
            </td>

            <td height="22" align="center" bgcolor="#a8c7ce">
                商城价格区间(元)：
            </td>
            <td bgcolor="#a8c7ce">
                <input type="text" name="minWebStorePrice" size="10" value=""/>-
                <input type="text" name="maxWebStorePrice" size="10" value=""/></td>
        </tr>

        <tr class="STYLE10">
            <td width="100" height="22" align="center" bgColor="#fff"></td>
            <td bgColor="#ffffff"><font face="宋体"
                                        color="red"> &nbsp;</font>
            </td>
            <td align="right" bgColor="#ffffff"><br>
                <br></td>
            <td align="right" bgColor="#ffffff">
                <button
                        type="submit" id="search" name="search" value="&#26597;&#35810;" class="button_view">&#26597;&#35810;
                </button> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <%--                <input type="submit" value="提交">--%>
                <input type="reset" name="reset" value="&#37325;&#32622;" class="button_view"/>
            </td>
        </tr>

        <%--（四）分页--%>
        <%--<tr>
            <td width="33%">
                <div align="left">
								<span class="STYLE22">&nbsp;&nbsp;&nbsp;&nbsp;共有<strong>
                                    &lt;%&ndash;前端属性跟实体属性一致&ndash;%&gt;
                                    ${requestScope.page.totalRecordsNum}</strong> 条记录，当前第<strong>${requestScope.page.currentPageNum }</strong> 页，共 <strong>${requestScope.page.totalPageNum }</strong> 页</span>
                </div>
            </td>

            <td width="49">
                <div align="center">
											<span class="STYLE22">
											<a href="${pageContext.request.contextPath }/admin/productServlet?op=searchPageProducts&num=1">首页</a>
											</span>
                </div>
            </td>
            <td width="49">
                <div align="center">
											<span class="STYLE22">
											<a href="${pageContext.request.contextPath }/admin/productServlet?op=searchPageProducts&num=${page.prevPageNum}"
                                               onclick="prevPage()">上一页</a>
											</span>
                </div>
            </td>
            <td width="49"><span class="STYLE22">
									    <div align="center">
											<span class="STYLE22">
											<a href="${pageContext.request.contextPath }/admin/productServlet?op=searchPageProducts&num=${page.nextPageNum}">下一页</a>
											</span>
										</div>
            </td>
            <td width="49">
                <div align="center">
                                            <span class="STYLE22"><a
                                                    href="${pageContext.request.contextPath }/admin/productServlet?op=searchPageProducts&num=${page.totalPageNum }">尾页</a></span>
                </div>
            </td>
            <td width="37" class="STYLE22">
                <div align="center">转到</div>
            </td>
            <td width="22">
                <div align="center">
                    <input type="text" name="num" id="num" value="${requestScope.page.currentPageNum }"
                           style="width:20px; height:12px; font-size:12px; border:solid 1px #7aaebd;"/>
                </div>
            </td>
            <td width="22" class="STYLE22">
                <div align="center">页</div>
            </td>
            <td width="35">
                <div align="center">
                                            <span class="STYLE22">
                                                <a style="cursor:pointer;" onclick="jump()">跳转</a></span>
                </div>

            </td>
        </tr>

    </table>
</form>

<script type="text/javascript">
    $().ready(function () {
        $("#checkbox11").click(function () {
            if ($(this).attr("checked")) {
                $(":checkbox").attr("checked", true);
            } else {
                $(":checkbox").attr("checked", false);
            }
        })
    });

    function prevPage() {
        if (${page.currentPageNum} ===
        1
    )
        {
            alert('已经是第一页！');
            return;
        }
    }

    function jump() {
        var num = document.getElementById("num").value;
        if (!/^[1-9][0-9]*$/.test(num)) {
            alert("请输入正确的页码");
            return;
        }
        if (num > ${page.totalPageNum}) {
            alert("页码超出范围");
            return;
        }

        window.location.href = "${pageContext.request.contextPath}/admin/categoryServlet?operation=findPageCategories" + "&num=" + num;
    }

</script>--%>
</body>

</html>


