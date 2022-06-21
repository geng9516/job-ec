//index用
function setEcSite() {
    $("#setEcSite").removeAttr("action");
    $("#setEcSite").attr("method", "post");
    $("#setEcSite").attr('action', "/setEcSite");
    $("#setEcSite").submit();
}
