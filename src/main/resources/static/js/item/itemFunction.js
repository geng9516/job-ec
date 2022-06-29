//shop名でアイテムを検索
function findIteminfoBySiteShop() {
    $("#findIteminfoBySiteShop").removeAttr("action");
    $("#findIteminfoBySiteShop").attr("method", "post");
    $("#findIteminfoBySiteShop").attr('action', "/findIteminfoBySiteShop");
    $("#findIteminfoBySiteShop").submit();
}

//是否编辑
function findEditedIteminfo() {
    $("#findEditedIteminfo").removeAttr("action");
    $("#findEditedIteminfo").attr("method", "post");
    $("#findEditedIteminfo").attr('action', "/findEditedIteminfo");
    $("#findEditedIteminfo").submit();
}

//修改一页表示件数
function setPageSize() {
    $("#setPageSize").removeAttr("action");
    $("#setPageSize").attr("method", "post");
    $("#setPageSize").attr('action', "/setPageSize");
    $("#setPageSize").submit();
}

//ダウンロードCSV
function downloadCsv() {
    $("#downloadCsvFrom").removeAttr("action");
    $("#downloadCsvFrom").attr("method", "post");
    $("#downloadCsvFrom").attr('action', "/bulkOperation");
    $("#downloadCsvFrom").submit();
}

//上传orderInfocsv
function csvImport() {
    $("#dataimpott").removeAttr("action");
    $("#dataimpott").attr("method", "post");
    $("#dataimpott").attr("enctype", "multipart/form-data");
    $("#dataimpott").attr('action', "/itemCsvImport");
    $("#dataimpott").submit();
}

function setEcSite() {
    $("#setEcSite").removeAttr("action");
    $("#setEcSite").attr("method", "post");
    $("#setEcSite").attr('action', "/setEcSite");
    $("#setEcSite").submit();
}

function selectItemPathFlog() {
    $("#selectItemPathFlog").removeAttr("action");
    $("#selectItemPathFlog").attr("method", "post");
    $("#selectItemPathFlog").attr('action', "/selectItemPathFlog");
    $("#selectItemPathFlog").submit();
}

//
function setIteminfos() {
    var checkedValue = $('tbody input:checked');
    var path = document.getElementById("path").value;
    var itemName = document.getElementById("itemName").value;
    //判断是否项目被选择
    if (typeof checkedValue.val() === "undefined") {
        alert("操作項目を選択してください！")
        return;
    }
    //保存选中的item的code
    var Values = new Array();
    //遍历
    checkedValue.each(function () {
        Values.push($(this).val());
    })
    $.ajax({
        url: '/setIteminfos',
        type: 'post',
        data: {"listString": Values,path,itemName},
        dataType: 'json',
        success: function (data) {
            window.location = "/iteminfo?pageNum=" + 1;
            // location.reload();
            $("#message").text(data);
        },
        error: function (data) {
            $("#message").val(data);
        }
    })
}

//全选和反选
function selectAll() {
    //id=all是全选/反选的checkbox，判断是选中还是没选中
    var isChecked = $("#allchecked").is(':checked');
    //获取所有checkbox组成的数组
    var checkedArrs = $("input[type='checkbox'].checkbox:checked");
    //判断是全选还是反选
    if (isChecked) {
        $("input[type='checkbox'].checkbox").each(function () {
            $(this).prop('checked', true);
        });
    } else {
        $("input[type='checkbox'].checkbox").each(function () {
            $(this).prop('checked', false);
        });
    }
}

//选中的产品下载iteminfo.csv 和照片拷贝
function bulkOperation() {
    var checkedValue = $('tbody input:checked');
    //判断是否项目被选择
    if (typeof checkedValue.val() === "undefined") {
        alert("操作項目を選択してください！")
        return;
    }
    //保存选中的item的code
    var Values = new Array();
    //遍历
    checkedValue.each(function () {
        Values.push($(this).val());
    })
    //被选中的是哪个操作
    var checkFlog = $("#downloadCsv").val();
    if (checkFlog === "") {
        return;
    }
    $.ajax({
        url: '/bulkOperation',
        type: 'post',
        data: {"listString": Values, "checkFlog": checkFlog},
        dataType: 'json',
        success: function (data) {
            window.location = "/iteminfo?pageNum=" + 1;
            $("#message").text(data);
        },
        error: function (data) {
            $("#message").val(data);
        }
    })
}

//多个返回列表
function setItemsFlogToEdit() {
    var checkedValue = $('tbody input:checked');
    //判断是否项目被选择
    if (typeof checkedValue.val() === "undefined") {
        alert("操作項目を選択してください！")
        return;
    }
    //保存选中的item的code
    var Values = new Array();
    //遍历
    checkedValue.each(function () {
        Values.push($(this).val());
    })
    $.ajax({
        url: '/setItemsFlogToEdit',
        type: 'post',
        data: {"listString": Values},
        dataType: 'json',
        success: function (data) {
            window.location = "/iteminfo?pageNum=" + 1;
            $("#message").text(data);
        },
        error: function (data) {
            $("#message").val(data);
        }
    })
}

//多个列入删除列表
function setDeleteItems() {
    var checkedValue = $('tbody input:checked');
    //判断是否项目被选择
    if (typeof checkedValue.val() === "undefined") {
        alert("操作項目を選択してください！")
        return;
    }
    //保存选中的item的code
    var Values = new Array();
    //遍历
    checkedValue.each(function () {
        Values.push($(this).val());
    })
    $.ajax({
        url: '/setDeleteItems',
        type: 'post',
        data: {"listString": Values},
        dataType: 'json',
        success: function (data) {
            window.location = "/iteminfo?pageNum=" + 1;
            $("#message").text(data);
        },
        error: function (data) {
            $("#message").val(data);
        }
    })
}

//多个列入删除列表
function setDeleteItems() {
    var checkedValue = $('tbody input:checked');
    //判断是否项目被选择
    if (typeof checkedValue.val() === "undefined") {
        alert("操作項目を選択してください！")
        return;
    }
    //保存选中的item的code
    var Values = new Array();
    //遍历
    checkedValue.each(function () {
        Values.push($(this).val());
    })
    $.ajax({
        url: '/setDeleteItems',
        type: 'post',
        data: {"listString": Values},
        dataType: 'json',
        success: function (data) {
            window.location = "/iteminfo?pageNum=" + 1;
            $("#message").text(data);
        },
        error: function (data) {
            $("#message").val(data);
        }
    })
}

//选中的产品删除
function deleteItems() {
    var checkedValue = $('tbody input:checked');
    //判断是否项目被选择
    if (typeof checkedValue.val() === "undefined") {
        alert("操作項目を選択してください！")
        return;
    }
    //保存选中的item的code
    var Values = new Array();
    //遍历
    checkedValue.each(function () {
        Values.push($(this).val());
    })
    $.ajax({
        url: '/deleteItems',
        type: 'post',
        data: {"listString": Values},
        dataType: 'json',
        success: function (data) {
            window.location = "/iteminfo?pageNum=" + 1;
            $("#message").text(data);
        },
        error: function (data) {
            $("#message").val(data);
        }
    })
}

//计算价格
function calculation(e) {
    //获取当前节点的父节点的父节点下的第一个子节点下的第一个子节点(送料)
    var value1 = e.parentNode.parentNode.children[1].children[0];
    //$(value1) 转为jQuery对象 操作dom
    var delivery = $(value1).find("option:selected").text();
    delivery = delivery.substring(delivery.indexOf(">") + 1, delivery.length);
    //进货价节点
    var value2 = e.parentNode.parentNode.children[1].children[1];
    var price = $(value2).val();
    //総仕入れ価格（元）
    var chinaDelivery = 6;
    var optionPrice = 10;
    var totolPurchasePrice = parseInt(delivery) + parseInt(price) + chinaDelivery + optionPrice;
    //総仕入れ価格（円）
    var kawase = 19.5;
    var japaTtotolPurchasePrice = totolPurchasePrice * kawase;
    //販売価格（円/税抜）
    //販売価格（円/税込）
    var riekiritu = 0.6;
    var tax = 0.9;
    var taxJapaTtotolPurchasePrice = ((japaTtotolPurchasePrice / riekiritu) / tax) + 300;
    //计算结果节点
    var value3 = e.parentNode.parentNode.children[1].children[2];
    if (taxJapaTtotolPurchasePrice === NaN) {
        $(value3).text(0);
    } else {
        $(value3).text(taxJapaTtotolPurchasePrice.toFixed(0));
    }

    //获取利益率节点
    var value3 = e.parentNode.parentNode.parentNode.parentNode.children[1].children[0].children[3].children[0];
    //获取販売価格节点
    var value4 = e.parentNode.parentNode.parentNode.parentNode.children[1].children[0].children[2].children[0];
    var salsePrice = parseInt($(value4).text());
    //计算结果节点
    var value5 = e.parentNode.parentNode.children[1].children[2];
    var salsePrice1 = parseInt($(value5).text());
    //販売価格不为空时
    if ($(value4).text() == null || $(value4).text() != "") {
        //消费税
        // var tax = (salsePrice + 850) * 0.1;
        var tax = 0;
        //站点费用
        var site = ((salsePrice + 850) * 0.35).toFixed(0);
        //利润
        var profit = (salsePrice + 850) - tax - site - japaTtotolPurchasePrice;
        //利益率
        var profitability = Number(profit / (salsePrice + 850) * 100).toFixed(1);
        profitability += "%";
        $(value3).text(profitability);
        //販売価格为空时
    } else {
        //消费税
        // var tax = (salsePrice1 + 850) * 0.1;
        var tax = 0;
        //站点费用
        var site = ((salsePrice1 + 850) * 0.35).toFixed(0);
        //利润
        var profit = (salsePrice1 + 850) - tax - site - japaTtotolPurchasePrice;
        //利益率
        var profitability = Number(profit / (salsePrice1 + 850) * 100).toFixed(1);
        profitability += "%";
        $(value3).text(profitability);
    }
}