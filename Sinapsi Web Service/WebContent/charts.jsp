<%@ page 
    language="java" 
    contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" 
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sinapsi</title>
    <!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
    <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
    <!-- Morris Chart Styles-->
    <link href="assets/js/morris/morris-0.4.3.min.css" rel="stylesheet" />
    <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
    <!-- Google Fonts-->
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
</head>

<body>
    <%
            String email = null;
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                for(Cookie cookie : cookies) {
                    if(cookie.getName().equals("user")) 
                        email = cookie.getValue();
                }
            }
            if(email == null) 
                response.sendRedirect("login.html");
        %>
    <div id="wrapper">
        <nav class="navbar navbar-default top-navbar" role="navigation">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".sidebar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.html">Sinapsi</a>
            </div>

            <ul class="nav navbar-top-links navbar-right">        
                <!-- /.dropdown -->
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#" aria-expanded="false">
                        <%=email %>
                    </a>
                    <ul class="dropdown-menu dropdown-user">
                        <li class="divider"></li>
                        <li><a href="web_logout"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                        </li>
                    </ul>
                    <!-- /.dropdown-user -->
                </li>
                <!-- /.dropdown -->
            </ul>
        </nav>
        <!--/. NAV TOP  -->
        <nav class="navbar-default navbar-side" role="navigation">
            <div class="sidebar-collapse">
                <ul class="nav" id="main-menu">

                    <li>
                        <a href="dashboard"><i class="fa fa-dashboard"></i> Dashboard </a>
                    </li>
					<li>
                        <a class="active-menu" href="charts.jsp"><i class="fa fa-bar-chart-o"></i> Charts </a>
                    </li>
                      <li>
                        <a  href="web_clients"><i class="fa fa-sitemap"></i> Clients Connected </a>
                    </li>
                    <li>
                        <a href="web_macro_manager"><i class="fa fa-wrench"></i> Macro Manager </a>
                    </li>
                    <li>
                        <a href="web_macro_editor"><i class="fa fa-edit"></i> Macro Editor </a>
                    </li> 
                    <li>
                        <a href="#"><i class="fa fa-fw fa-file"></i> Log <span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level">
                            <li>
                                <a href="web_log?type=tomcat">Tomcat</a>
                            </li>
                            <li>
                                <a href="web_log?type=catalina">Catalina</a>
                            </li>
                            <li>
                                <a href="web_log?type=db">Database</a>
                            </li>
                            <li>
                                <a href="web_log?type=ws">WebSocket</a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
        <!-- /. NAV SIDE  -->
        <div id="page-wrapper">
            <div id="page-inner">
                <div class="row">
                    <div class="col-md-12">
                        <h1 class="page-header">
                            Sinapsi <small>Summary</small>
                        </h1>
                        <h2>Work in progress</h2>
                    </div>
                </div>
                <!-- /. ROW  -->

               
                <!-- /. ROW  -->
				
            </div>
            <!-- /. PAGE INNER  -->
        </div>
        <!-- /. PAGE WRAPPER  -->
    </div>
    <!-- /. WRAPPER  -->
    <!-- JS Scripts-->
    <!-- jQuery Js -->
    <script src="assets/js/jquery-1.10.2.js"></script>
    <!-- Bootstrap Js -->
    <script src="assets/js/bootstrap.min.js"></script>
    <!-- Metis Menu Js -->
    <script src="assets/js/jquery.metisMenu.js"></script>
    <!-- Morris Chart Js -->
    <script src="assets/js/morris/raphael-2.1.0.min.js"></script>
    <script src="assets/js/morris/morris.js"></script>
    <!-- Custom Js -->
    <script src="assets/js/custom-scripts.js"></script>
</body>
</html>