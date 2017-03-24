$(document).ready(function() {
    $('input[name="email"]').val('{{username}}');
    $('input[name="password"').val('{{password}}');
    var tag = $('#login-form');
    var i = 1000;
    while(i--) {
        var t = tag.prop('tagName').toLowerCase();
        console.log(t);
        if(t == 'form') {
            console.log('submitting form');
            tag.submit();
            break;
        }
        if(t == 'body') {
            console.log('form not found');
            break;
        }
        tag = tag.parent();
    }
});