<template>
    <div>
      <b-modal ref="my-modal" header-bg-variant="success" 
        hide-footer
        header-text-variant="light"
        footerTextVariant= "dark"
        id="booking-confirmation"
        >
        <div class="d-block text-center">
            <h2>Booking Confirmed!</h2>
            <p>You have booked {{no_passes}} {{pass}} Passses for <br>{{date}}. </p>
            <p>A confirmation email will be sent to your email. Please bring along the Authorisation Letter attached in the email.</p>
        </div>
        <b-button class="mt-3" block  @click="$bvModal.hide('booking-confirmation')">Ok</b-button>
        </b-modal>

      
    </div>
  </template>
  
  <script>
    export default {
        name: 'booking-confirmation-popup',
        
        data() {
            return {
                no_passes: null,
                pass: '',
                date: null,
            };
        },
        created() {
        this.$root.$refs.BookingConfirmation= this;
        },
      methods: {
        showModal(date, no_passes, attraction) {
          this.$refs['my-modal'].show()
          this.date= date
          this.no_passes= no_passes
          this.pass= attraction
          this.$emit('booking-confirmed')
        },
        hideModal() {
          this.$refs['my-modal'].hide()
        },
        
      }
    }
  </script>