<%@ page import="java.util.Vector"%>
<%@ page import="com.sinapsi.utils.Pair"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Sinapsi</title>

<link href="assets/css/bootstrap.css" rel="stylesheet" />
<link href="assets/css/font-awesome.css" rel="stylesheet" />
<link href="assets/css/custom-styles.css" rel="stylesheet" />
<link href='http://fonts.googleapis.com/css?family=Lato:300,400,700'
	rel='stylesheet' type='text/css' />
<script src="assets/js/angular.js"></script>
<script src="assets/js/d3.js"></script>
<script src="assets/js/nv.d3.js"></script>
<script src="assets/js/moment.js"></script>
<script src="assets/js/angularjs-nvd3-directives.js"></script>
<link rel="stylesheet" href="assets/css/nv.d3.css" />
<%
  @SuppressWarnings("unchecked")
  Vector<Pair<String, String>> load = (Vector<Pair<String, String>>) session.getAttribute("server_load");
  String role = (String) session.getAttribute("role");
%>
<%
  if(role == "admin") {
%>
<script>
    var app = angular.module("nvd3TestApp", ['nvd3ChartDirectives']);

    function ExampleCtrl($scope){
      $scope.exampleData = [
        {
          "key": "Server Load",
          "values": [
            <%for(int i = 0; i < load.size(); ++i) {%>
            <%if(i + 1 == load.size()) {%>
              [<%=load.get(i).getFirst()%>.0, <%=load.get(i).getSecond()%>.0]
            <%} else {%>
              [<%=load.get(i).getFirst()%>.0, <%=load.get(i).getSecond()%>.0],
            <%} // else if
              } // for%>
          ]
        } 
      ];
            
            
      $scope.colorFunction = function() {
        return function(d, i) {
          return '#E01B5D';
        }
      };
            
      $scope.toolTipContentFunction = function() {
        return function(key, x, y, e, graph) {
          return  '<p>' +  y + ' at ' + x + '</p>';
        }
      };
            
      $scope.xAxisTickFormat = function(){
        return function(d) {
          return d3.time.format('%x')(new Date(d));  //uncomment for date format
        }
      };
    }
  </script>
<%
  } // if ( role == admin)
%>
</head>

<%
  if (role == "admin") {
%>
<body ng-app='nvd3TestApp'>

<%
  } if (role == "user") {
%>
<body>
<%
	}
%>
	<%
	  String email = null;
	  Cookie[] cookies = request.getCookies();
	  if (cookies != null) {
	    for (Cookie cookie : cookies) {
	      if (cookie.getName().equals("user"))
	        email = cookie.getValue();
	    }
	  }
	  if (email == null)
	    response.sendRedirect("login.html");
	%>
	<div id="wrapper">
		<nav class="navbar navbar-default top-navbar" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".sidebar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.jsp">Sinapsi</a>
			</div>

			<ul class="nav navbar-top-links navbar-right">
				<!-- /.dropdown -->
				<li class="dropdown"><a class="dropdown-toggle"
					data-toggle="dropdown" href="#" aria-expanded="false"> <%=email%>
				</a>

					<ul class="dropdown-menu dropdown-user">
						<li class="divider"></li>
						<li><a href="web_logout"><i class="fa fa-sign-out fa-fw"></i>
								Logout</a></li>
					</ul></li>
			</ul>
		</nav>

		<nav class="navbar-default navbar-side" role="navigation">
			<div class="sidebar-collapse">
				<ul class="nav" id="main-menu">

					<li><a href="dashboard"><i class="fa fa-dashboard"></i>
							Dashboard </a></li>
					<%
					  if (role == "admin") {
					%>
					<li><a class="active-menu" href="charts.jsp"><i
							class="fa fa-bar-chart-o"></i> Charts </a></li>
					<li><a href="web_clients"><i class="fa fa-sitemap"></i>
							Clients Connected </a></li>
					<%
					  }
					%>
					<li><a href="web_macro_manager"><i class="fa fa-wrench"></i>
							Macro Manager </a></li>
					<li><a href="web_macro_editor"><i class="fa fa-edit"></i>
							Macro Editor </a></li>
					<li><a href="web_devices"><i class="fa fa-desktop"></i>
              Devices </a></li>
					<%
					  if (role == "admin") {
					%>
					<li><a href="#"><i class="glyphicon glyphicon-cog"></i>
							Engine <span class="fa arrow"></span></a>
						<ul class="nav nav-second-level">
							<li><a href="web_log?type=actionlog">Log</a></li>
						</ul></li>
						
					<li><a href="#"><i class="fa fa-fw fa-file"></i> Log <span
							class="fa arrow"></span></a>
						<ul class="nav nav-second-level">
							<li><a href="web_log?type=tomcat">Tomcat</a></li>
							<li><a href="web_log?type=catalina">Catalina</a></li>
							<li><a href="web_log?type=db">Database</a></li>
							<li><a href="web_log?type=ws">WebSocket</a></li>
							<li><a href="web_log?type=webs">Web Service</a></li>
						</ul></li>
					<%
					  }
					%>
				</ul>
			</div>
		</nav>
		<!-- /. NAV SIDE  -->
		<div id="page-wrapper">
			<div id="page-inner">
				<div class="row">
					<div class="col-md-12">
						<h1 class="page-header">
							Sinapsi <small>charts</small>
						</h1>
					</div>
				</div>
				<%
				  if (role == "user") {
				%>
				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">Messages</div>
							<div class="panel-body">
								<div class="alert alert-danger">
									You need <strong>administration</strong> permisison to see this
									page.
								</div>
							</div>
						</div>
					</div>
				</div>
				<%
				  }
				  if (role == "admin") {
				%>
				<!-- /. ROW  -->
				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12" ng-app='nvd3TestApp'>
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="panel panel-default">
								<div class="panel-heading">Server Load</div>
								<div class="panel-body">
									<div ng-controller="ExampleCtrl">
										<nvd3-line-chart data="exampleData" id="exampleId" width="800"
											height="400" showXAxis="true" showYAxis="true"
											tooltips="true" tooltipcontent="toolTipContentFunction()"
											interactive="true" color="colorFunction()"
											xAxisTickFormat="xAxisTickFormat()" interpolate="cardinal"
											useInteractiveGuideLine="true">
										<svg></svg> </nvd3-line-chart>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<%
				  }
				%>
			</div>
			pre-alpha 1.0 version © 2015 Sinapsi
		</div>
	</div>
	<script src="assets/js/jquery-1.10.2.js"></script>
	<script src="assets/js/bootstrap.min.js"></script>
	<script src="assets/js/jquery.metisMenu.js"></script>
	<script src="assets/js/custom-scripts.js"></script>
</body>
</html>