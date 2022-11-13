<template>
    <div>

        <b-form @submit.stop.prevent="onSubmit">
            <b-form-group id="example-input-group-1" label="Contact Number" label-for="example-input-1">
                <b-form-input id="example-input-1" name="example-input-1" v-model="$v.form.contact.$model"
                    :state="validateState('contact')" aria-describedby="input-1-live-feedback"></b-form-input>

                <b-form-invalid-feedback id="input-1-live-feedback">This is a required field and only numeric values are
                    accepted.</b-form-invalid-feedback>
            </b-form-group>

            <b-form-group id="example-input-group-2" label="Password" label-for="example-input-3">
                <b-form-input id="example-input-2" name="example-input-2" v-model="$v.form.password.$model"
                    :state="validateState('password')" aria-describedby="input-2-live-feedback"></b-form-input>

                <b-form-invalid-feedback id="input-2-live-feedback">This is a required field and the minimum length is
                    6.
                </b-form-invalid-feedback>
            </b-form-group>

            <b-form-group id="example-input-group-3" label="Confirm Password" label-for="example-input-3">
                <b-form-input id="example-input-3" name="example-input-3" v-model="$v.form.cpassword.$model"
                    :state="validateState('cpassword')" aria-describedby="input-3-live-feedback"></b-form-input>

                <b-form-invalid-feedback id="input-3-live-feedback">This is a required field and password must be the
                    same as the previous field.
                </b-form-invalid-feedback>
            </b-form-group>

            <b-form-group id="example-input-group-4" label="Check your email for the token and paste it here"
                label-for="example-input-4">
                <b-form-input id="example-input-4" name="example-input-4" v-model="$v.form.token.$model"
                    :state="validateState('token')" aria-describedby="input-4-live-feedback"></b-form-input>

                <b-form-invalid-feedback id="input-4-live-feedback">This is a required field and the minimum length is
                    6.
                </b-form-invalid-feedback>
            </b-form-group>

            <div class="group">
                <label for="link" id="countdown" class="label"></label>
            </div>

            <input type="button" class="btn btn-outline-primary btnDisable" id="myBtn" value="Resend activation link"
                @click="resend">

            <br>
            <br>

            <b-button type="submit" variant="primary">Register</b-button>

        </b-form>
    </div>
</template>
  
<script>
import axios from "axios";
import { validationMixin } from "vuelidate";
import { required, minLength, numeric, sameAs } from "vuelidate/lib/validators";

export default {
    mixins: [validationMixin],
    data() {
        props: ["email"]
        return {
            form: {
                email: null,
                password: null,
                cpassword: null,
                activation: null,
            },
            api: {
                confirm: "http://localhost:8080/auth/confirm",
            }
        };
    },
    validations: {
        form: {
            contact: {
                required,
                numeric
            },
            password: {
                required,
                minLength: minLength(6),
            },
            cpassword: {
                required,
                sameAsPassword: sameAs("password"),
                minLength: minLength(6),
            },
            token: {
                required
            }
        }
    },
    methods: {
        validateState(contact) {
            const { $dirty, $error } = this.$v.form[contact];
            return $dirty ? !$error : null;
        },
        validateState(password) {
            const { $dirty, $error } = this.$v.form[password];
            return $dirty ? !$error : null;
        },
        validateState(cpassword) {
            const { $dirty, $error } = this.$v.form[cpassword];
            return $dirty ? !$error : null;
        },
        validateState(token) {
            const { $dirty, $error } = this.$v.form[token];
            return $dirty ? !$error : null;
        },
        // resetForm() {
        //   this.form = {
        //     email: null,
        //     food: null
        //   };

        //   this.$nextTick(() => {
        //     this.$v.$reset();
        //   });
        // },

        onSubmit() {
            this.$v.form.$touch();
            if (this.$v.form.$anyError) {
                return;
            }
            // alert("Form submitted!");

            return axios
                .get(this.api.confirm, {
                    "contact": this.form.contact,
                    "password": this.form.password,
                    "cpassword": this.form.cpassword,
                    "token": this.form.token,
                })
                .then((response) => {
                    console.log(response.data);
                    this.$router.push("/login");
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        resend(event) {
            event.preventDefault()
            alert("Please check your email for the token")
            return axios
                .post("http://localhost:8080/auth/resend", {
                    contact: this.form.contact,
                })
                .then((response) => {
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log(error);
                });
        }
    }

};

var timeleft = 10;
var downloadTimer = setInterval(function () {
    const btn = document.getElementById("myBtn")
    if (timeleft <= 0) {
        clearInterval(downloadTimer);
        btn.disabled = false;
        document.getElementById("countdown").innerHTML = "";
    } else {
        btn.disabled = true;
        document.getElementById("countdown").innerHTML = "Didn't receive the token? Resend in " + timeleft + " seconds";
    }
    timeleft -= 1;
}, 1000);

</script>

<style>
body {
    padding: 1rem;
}

.btnDisable {
    cursor: wait;
}
</style>