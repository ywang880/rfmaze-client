<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<script type="text/javascript" language="javascript" src="js/rfmaze.js"></script>

<style>

.matrix_fullsize {
    font-size: 12px;
    color: #000;
    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif
}

.matrix_fullsize th {
    width: 60px;
    max-width:60px;
    overflow: hidden;
    text-overflow: ellipsis;
    border: 1px solid #789;
    padding: 5px
}

.matrix_fullsize td {
    width: 60px;
    max-width:60px;
    border: 1px solid #789;
    padding: 5px
}

.matrix_fullsize tbody tr td {
    background-color: #eef2f9;
    background-image: -moz-linear-gradient(top, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.0) 100%);
    background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, rgba(255, 255, 255, 0.4)), color-stop(100%, rgba(255, 255, 255, 0.0)))
}

.matrix_fullsize tbody tr.odd td {
    background-color: #d6e0ef;
    background-image: -moz-linear-gradient(top, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.0) 100%);
    background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, rgba(255, 255, 255, 0.4)), color-stop(100%, rgba(255, 255, 255, 0.0)))
}
.matrix_fullsize thead tr th,
.matrix_fullsize thead tr td,
.matrix_fullsize tfoot tr th,
.matrix_fullsize tfoot tr td {
    background-color: #8ca9cf;
    background-image: -moz-linear-gradient(top, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.0) 100%);
    background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, rgba(255, 255, 255, 0.4)), color-stop(100%, rgba(255, 255, 255, 0.0)));
    font-weight: 700
}
</style>
