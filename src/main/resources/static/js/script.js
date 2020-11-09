function showStatus(message,timeout,add)
{
    $('.statusBar').text(message);
    if (typeof _statusbar == "undefined")
    {
        // ** Create a new statusbar instance as a global object
        _statusbar =
            $("<div id='_statusbar' class='statusbar barGreen'></div>")
                .appendTo(document.body)
                .show();
    }

    if (add)
    // *** add before the first item
        _statusbar.prepend( "<div style='margin-bottom: 2px;' >" + message + "</div>")[0].focus();
    else
        _statusbar.text(message)

    _statusbar.show();

    if (timeout)
    {
        setTimeout( function() {_statusbar.fadeOut(4000); },timeout);
    }
}

function showAlert(message,timeout,add)
{
    $('.statusBar').text(message);
    if (typeof _statusbar == "undefined")
    {
        // ** Create a new statusbar instance as a global object
        _statusbar =
            $("<div id='_statusbar' class='statusbar barRed'></div>")
                .appendTo(document.body)
                .show();
    }

    if (add)
    // *** add before the first item
        _statusbar.prepend( "<div style='margin-bottom: 2px;' >" + message + "</div>")[0].focus();
    else
        _statusbar.text(message)

    _statusbar.show();

    if (timeout)
    {
        setTimeout( function() {_statusbar.fadeOut(4000,function(){}); },timeout);
    }
}
                                            /*  code for Jquery validation  */


// define phone number regex
jQuery.validator.addMethod("phoneIR", function (phone_number, element) {
    phone_number = phone_number.replace(/\s+/g, "");
    return this.optional(element) || phone_number.length > 9 &&
        phone_number.match('^([0|+[0-9]{1,5})?([7-9][0-9]{9})$');
}, "شماره تلفن را صحیح وارد کنید با صفر شروع شود");

//define Persian and arabic name regex with space
jQuery.validator.addMethod("nameIR", function (value, element) {
    return this.optional(element) || /^[\u0600-\u06FF\u08A0-\u08FF\s]+$/i.test(value);
});

jQuery.validator.addMethod("alphanumeric", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9 ]+$/.test(value);
});



// Add a custom class to your name mangled input and add rules like this
var requierd = {
    required: true,

    messages: {
        required: 'این فیلد اجباری است'

    }
};
var Vname = {

    nameIR: true,
    messages: {

        nameIR: 'صحیح وارد کنید - فقط حروف فارسی و فاصله'
    }
};
var VuserName = {

    alphanumeric: true,
    required: true,
    minlength:4,
    messages: {

        alphanumeric: 'صحیح وارد کنید - فقط حروف انگلیسی و فاصله',
        required:'این فیلد اجباری است',
        minlength:'حداقل 4 کارکتر وارد کنید'
    }
};

var phone = {
    phoneIR: true,
    messages: {
        phoneIR: "شماره تلفن را صحیح وارد کنید"
    }
};
var Vnumber = {
    number: true,
    messages: {
        number: "عدد وارد کنید"
    }
};
