<template>

  <div>
    <ul class="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
      <li class="nav-item" role="presentation">
        <a class="nav-link" id="tab-login" data-mdb-toggle="pill" href="#pills-login" role="tab"
          aria-controls="pills-login" aria-selected="false" @click="login()">Login</a>
      </li>
      <li class="nav-item" role="presentation">
        <a class="nav-link active" id="tab-register" data-mdb-toggle="pill" href="#pills-register" role="tab"
          aria-controls="pills-register" aria-selected="true">Register</a>
      </li>
    </ul>

    <b-form @submit.stop.prevent="onSubmit">
      <b-form-group id="example-input-group-1" label="Name" label-for="example-input-1">
        <b-form-input id="example-input-1" name="example-input-1" v-model="$v.form.name.$model"
          :state="validateState('name')" aria-describedby="input-1-live-feedback"></b-form-input>

        <b-form-invalid-feedback id="input-1-live-feedback">This is a required field and must not be longer than 50
          characters.</b-form-invalid-feedback>
      </b-form-group>

      <b-form-group id="example-input-group-3" label="Email" label-for="example-input-3">
        <b-form-input id="example-input-3" name="example-input-3" v-model="$v.form.email.$model"
          :state="validateState('email')" aria-describedby="input-3-live-feedback"></b-form-input>

        <b-form-invalid-feedback id="input-3-live-feedback">This is a required field and must end with @nysi.org.sg or
          @sportsschool.edu.sg
        </b-form-invalid-feedback>
      </b-form-group>

      <b-button type="submit" variant="primary">Get Activation Link</b-button>

      <!-- <b-button class="ml-2" @click="resetForm()">Reset</b-button> -->

      <!-- <b-button variant="primary" v-b-modal.modal-prevent-closing>Get Activation Link</b-button> -->
      <!-- <b-modal id="modal-prevent-closing" ref="modal" title="Check your email for activation link" @show="resetModal"
        @hidden="resetModal" @ok="handleOk">
        <form ref="form" @submit.stop.prevent="handleSubmit">
          <b-form-group label="Paste activation link" label-for="name-input" invalid-feedback="Link is required"
            :state="nameState">
            <b-form-input id="name-input" v-model="name" :state="nameState" required></b-form-input>
          </b-form-group>
        </form>
      </b-modal> -->
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
import { required, maxLength, helpers } from "vuelidate/lib/validators";
const email_validation = helpers.regex('email', /(?:[a-z0-9]+@nysi.org.sg|[a-z0-9]+@sportsschool.edu.sg)/);
import authentication from "/Authentication.vue";

export default {
  mixins: [validationMixin],
  data() {
    return {
      form: {
        name: '',
        email: '',
      },
      api: {
        register: 'http://localhost:8080/auth/signup',
      },
    };
  },
  validations: {
    form: {
      name: {
        required,
        maxLength: maxLength(50)
      },
      email: {
        required,
        email_validation
      }
    }
  },
  components: {
    authentication
  },
  methods: {
    validateState(name) {
      const { $dirty, $error } = this.$v.form[name];
      return $dirty ? !$error : null;
    },
    validateState(email) {
      const { $dirty, $error } = this.$v.form[email];
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
      // this.$router.push('/authentication')
      // console.log(this.form.email);
      // alert("Form submitted!");

      return axios
        .post(this.api.register, {
          "name": this.form.name,
          "email": this.form.email,
        })
        .then((response) => {
          console.log(response.data);
          this.$router.push('/authentication')
        })
        .catch((error) => {
          console.log(error);
        });
    },
    login() {
      this.$router.push('/login')
    }
  }
};
</script>