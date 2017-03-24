$(document).ready(function() {
    $('input.form-submit').each(function() {
        if(this.value.toLowerCase().indexOf('free') !== -1) {
            this.click();
        }
    });
});