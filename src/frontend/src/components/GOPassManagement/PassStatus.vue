<template>
    <div style="padding-bottom: 10%">
        <b-form @submit="onSubmit">
            <b-form-group id="input-group-1" label="Enter Email address:" label-for="input-1">
                <b-form-input id="input-1" v-model="form.email" type="email" placeholder="Email Address" required>
                </b-form-input>
            </b-form-group>

            <b-button type="submit" variant="primary" class="mb-4">Submit</b-button>
        </b-form>

        <div class="row gx-3" v-if="bookingList[0].attractionName.length > 0">
            <div class="card col-md-5 mx-4" v-for="(booking, b) in bookingList" :key="b"> 
                <h4 class="card-header font-weight-bold">
                    {{ booking.attractionName }}
                </h4>
                <div class="card-body">
                    <p class="card-text">Loan Date: {{ booking.loanDate.split("T")[0] }}</p>
                    <p class="card-text">Pass ID: {{ booking.passId }}</p>
                    <p class="card-text">Booking Status: {{ booking.status }}</p>
                    <b-button v-b-modal.modal-1 class="btn-sm mt-1" variant="secondary" @click="sendInfo(booking.loanID); getPassStatus(booking.passId)">
                        Edit Pass Status
                    </b-button>
                </div>
            </div>
        </div>
        <b-modal id="modal-1" title="Update Pass Status" alignment="center">
            <p class="card-text">Current Pass Status: {{ passLocation }}</p>
            <p class="card-text">Current Booking Status: {{ booking.status }}</p>

            <template #modal-footer>
                <b-button variant="secondary" @click="passStatus(bookingId, 'INOFFICE'); $bvModal.hide('modal-1')">
                    Returned</b-button>
                <b-button variant="secondary" @click="passStatus(bookingId, 'INOFFICE'); $bvModal.hide('modal-1')">
                Returned</b-button>
                <b-button variant="success" @click="passStatus(bookingId, 'ONLOAN'); $bvModal.hide('modal-1')">Collected
                </b-button>
            </template>
        </b-modal>
    </div>
</template>
  
<script>

import axios from "axios";

export default {
    data() {
        return {
            form: {
                email: '',
            },
            bookingList: [{ attractionName: "", loanDate: "", passId: "", status: "", loanID: "" }],
            bookingId: "",
            passLocation: "",
            api: {
                bookingList: "http://localhost:8080/loan/",
                passStatus: "http://localhost:8080/passstatus/change",
                emailCollected: "http://localhost:8080/email/collected",
                passDetails: "http://localhost:8080/pass/",
            }
        }
    },

    created() {
        this.passLocation = this.getPassStatus(this.passId);
        this.user = JSON.parse(localStorage.getItem('user'));
    },

    methods: {
        onSubmit(event) {
            event.preventDefault()
            return axios
                .get(this.api.bookingList + this.form.email, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
                })
                .then((response) => {
                    console.log(response.data)
                    this.bookingList = response.data;
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        sendInfo(loanID) {
            this.bookingId = loanID;
        },

        passStatus(bookingId, status) {
            console.log(bookingId);
            console.log(status);
            return axios
                .put(this.api.passStatus, {
                    "loanId": bookingId,
                    "passStatus": status,
                },
                {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
                })
                .then((response) => {
                    console.log(response.data)
                    this.$alert(response.data);
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        async getPassStatus(passId) {
            console.log(passId)
            await axios
                .get(this.api.passDetails + passId, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
                })
                .then((response) => {
                    // console.log(response.data.passStatus)
                    this.passLocation = response.data.passStatus;
                    console.log(this.passLocation)
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },
    },
}
</script>