<template>
  <div style="margin-top: 5%; margin-left: 10%; margin-right: 10%">
    <div><img src="../assets/logo.png" class=" mx-auto my-5 d-block"></div>

    <ul class="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
      <li class="nav-item" role="presentation">
        <a class="nav-link active" id="tab-login" data-mdb-toggle="pill" href="#" role="tab" aria-controls="pills-login"
          aria-selected="true">Login</a>
      </li>
      <li class="nav-item" role="presentation">
        <a class="nav-link" id="tab-register" data-mdb-toggle="pill" href="" role="tab" aria-controls="pills-register"
          aria-selected="false" @click="register()">Register</a>
      </li>
    </ul>

    <b-form @submit.stop.prevent="onSubmit">
      <b-form-group id="example-input-group-1" label="Email" label-for="example-input-1">
        <b-form-input id="example-input-1" name="example-input-1" v-model="$v.form.email.$model"
          :state="validateState('email')" aria-describedby="input-1-live-feedback"></b-form-input>

        <b-form-invalid-feedback id="input-1-live-feedback">This is a required field and must end with @nysi.org.sg or
          @sportsschool.edu.sg</b-form-invalid-feedback>
      </b-form-group>

      <div> </div>

      <b-form-group id="example-input-group-3" label="Password" label-for="example-input-3">
        <b-form-input id="example-input-3" name="example-input-3" v-model="$v.form.password.$model"
          :state="validateState('password')" aria-describedby="input-3-live-feedback"></b-form-input>

        <b-form-invalid-feedback id="input-3-live-feedback">This is a required field and the minimum length is 6.
        </b-form-invalid-feedback>
      </b-form-group>

      <!-- <b-form-group id="example-input-group-4" label-for="example-input-4">
        <b-form-checkbox id="checkbox-1" v-model="status" name="checkbox-1" value="remember_me"
          unchecked-value="forget_me">
          Remember me
        </b-form-checkbox>
      </b-form-group> -->

      <b-button type="submit" variant="primary">Login</b-button>

      <br>

      <!-- Forget password?
      <b-button class="ml-2" @click="forgetPassword()">Reset Password</b-button> -->

    </b-form>
  </div>
</template>

<style>
body {
  padding: 1rem;
}
</style>

<script>
import axios from "axios";
import { validationMixin } from "vuelidate";
import { required, minLength, helpers } from "vuelidate/lib/validators";
const email_validation = helpers.regex('email', /(?:[a-z0-9]+@nysi.org.sg|[a-z0-9]+@sportsschool.edu.sg)/);

export default {
  mixins: [validationMixin],
  data() {
    return {
      user_details: '',
      form: {
        email: '',
        password: '',
      },
      status: 'forget_me',
      api: {
        sign_in: 'http://localhost:8080/auth/signin',
        reset: 'http://localhost:8080/auth/reset',
      }
    };
  },
  validations: {
    form: {
      email: {
        required,
        // email_validation
      },
      password: {
        required,
        // minLength: minLength(6)
      }
    }
  },
  methods: {
    validateState(email) {
      const { $dirty, $error } = this.$v.form[email];
      return $dirty ? !$error : null;
    },
    validateState(password) {
      const { $dirty, $error } = this.$v.form[password];
      return $dirty ? !$error : null;
    },

    onSubmit() {
      this.$v.form.$touch();
      // if (this.$v.form.$anyError) {
      //   return;
      // }
      axios
        .post(this.api.sign_in, {
          "email": this.form.email,
          "password": this.form.password,
        })
        .then(response => {
          console.log(response.data);
          //store jwt into local storage 

          this.user_details= response.data
          localStorage.setItem('user', JSON.stringify(response.data));

          this.$router.push('/booking');
        })
        .catch(error => {
          console.log(error);
          this.$alert("Invalid email or password")
        });
    },

    register() {
      this.$router.push('/register');
    },

    forgetPassword() {
      return axios
        .post(this.api.reset, {
          "email": this.form.email
        })
        .then(response => {
          console.log(response.data);
          this.$router.push('/reset');
        })
        .catch(error => {
          console.log(error);
        });
    }
  },
};
</script>