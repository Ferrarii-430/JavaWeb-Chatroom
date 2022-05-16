$(function () {

    setTimeout(function () {
        // $('#disconnected').modal('show');
        // $('#call').modal('show');
        // $('#videoCall').modal('show');
        $('#pageTour').modal('show');
    }, 1000);


    $(document).on('click', '.layout .content .sidebar-group .sidebar .list-group-item', function () {
        if (jQuery.browser.mobile) {
            $(this).closest('.sidebar-group').removeClass('mobile-open');
        }
    });

});


$('#pageTour').on('hidden.bs.modal', function () {
    setTimeout(chatheadnone,1000);
})

//绑定教程事件
$(document).delegate(".enjoyhint-step-4 .enjoyhint_next_btn","click",function (){
    chathead_inline_display();//显示3个按钮
})
//绑定教程事件
$(document).delegate(".enjoyhint-step-7 .enjoyhint_next_btn","click",function (){
    chathead_inline_none();//显示3个按钮
})
//绑定教程事件
$(document).delegate(".enjoyhint .enjoyhint_skip_btn","click",function (){
    chathead_inline_none();//显示3个按钮
})
//绑定教程事件
$(document).delegate(".enjoyhint .enjoyhint_close_btn","click",function (){
    chathead_inline_none();//显示3个按钮
})