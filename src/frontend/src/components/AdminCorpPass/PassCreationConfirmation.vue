<template>
    <div>
      <b-modal ref="pass-creation-confirmation-modal" header-bg-variant="success" 
        hide-footer
        header-text-variant="light"
        footerTextVariant= "dark"
        id="pass-creation-confirmation"
        >
        <div class="d-block text-center">
            <h2>Passes successfully created!</h2>
            <p>You have created {{no_passes}} new passes for {{pass_name}}</p>
        </div>
        <b-button class="mt-3" block  @click="$bvModal.hide('pass-creation-confirmation')">Ok</b-button>
        </b-modal>

      
    </div>
  </template>
  
  <script>
    export default {
        name: 'pass-creation-confirmation-popup',
        
        data() {
            return {
                no_passes: null,
                pass_name: null,
            };
        },
        created() {
        this.$root.$refs.PassCreationConfirmation= this;
        },
      methods: {
        showModal(no_passes, pass_name) {
          this.$refs['pass-creation-confirmation-modal'].show()
          this.pass_name= pass_name
          this.no_passes= no_passes
        },
        hideModal() {
          this.$refs['pass-creation-confirmation-modal'].hide()
        },
        
      }
    }
  </script>