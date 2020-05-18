/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
(function(){
    function readURL(input) {

        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function (e) {
                $('.avatar-bg').css({
                    'background':'url('+e.target.result+')',
                    'background-size':'cover',
                    'background-position': '50% 50%'
                });
            };

            reader.readAsDataURL(input.files[0]);
        }
    }
    
    function toggleAlert(clasz, display){
        $(".alert")
            .removeClass("display")
            .removeClass("alert-info")
            .removeClass("alert-success")
            .removeClass("alert-danger")
            .addClass(clasz);
        if(display){
            $(".alert").addClass("display")
        }
        if(clasz === "alert-success"){
            $(".alert > span").text('Profile saved');
        }else if(clasz === "alert-danger"){
            $(".alert > span").text('Profile reset');
        }
    }

    $("input.form-control[name=avatar-file]").change(function(){
        readURL(this);
    });
    
    // $('#profile').delegate('form', 'submit', function (e) {
    //     var inst = this;
    //     var formData = new FormData($(this)[0]);
    //
    //     $(inst).find("button[type = submit]").addClass("loading").prop("disabled", true);
    //     toggleAlert("alert-success", true);
    //
    //     setTimeout(function(){
    //         $(inst).find("button[type = submit]").removeClass("loading").prop("disabled", false);
    //         toggleAlert("alert-success");
    //     },1000);
    //
    //     return false;
    // });
    //
    $('#profile').delegate('form', 'reset', function (e) {
        var inst = this;
        var formData = new FormData($(this)[0]);

        $(inst).find("button[type = reset]").addClass("loading").prop("disabled", true);
        toggleAlert("alert-danger",true);
        
        setTimeout(function(){
            $(inst).find("button[type = reset]").removeClass("loading").prop("disabled", false);
            toggleAlert("alert-danger");
        },1000);
        
        return false;
    });
})()
