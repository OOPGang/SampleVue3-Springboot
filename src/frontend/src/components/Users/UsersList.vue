<template>
    <div class="users-admin">
        <div class="container">
            <div class="row">
                <div class="col">
                    <h2>Employee Management</h2>
                </div>
                <div class="col text-right">
                    <b-dropdown id="dropdown-buttons" text="Filter By" variant="secondary">
                        <b-dropdown-item-button @click="getUsers()">View All Users
                        </b-dropdown-item-button>
                        <b-dropdown-item-button @click="filterByType('STAFF')">View Staff Only
                        </b-dropdown-item-button>
                        <b-dropdown-item-button @click="filterByType('ADMIN')">View Admin Only
                        </b-dropdown-item-button>
                        <b-dropdown-item-button @click="filterByType('GENERALOFFICE')">View GO Only
                        </b-dropdown-item-button>
                    </b-dropdown>
                </div>
            </div>

            <div>
                <table class="table text-nowrap">
                    <thead class="thead-dark">
                        <tr>
                            <th scope="col" id="Name">Name</th>
                            <th scope="col" id="Email">Email</th>
                            <th scope="col" id="UserType">User Type</th>
                            <th scope="col" id="OutstandingFees">Outstanding Fees</th>
                            <th scope="col" id="UserStatus">Status</th>
                            <th scope="col" id="ChangeStatus">Change User Status</th>
                            <th scope="col" id="ChangeType">Change User Type</th>
                        </tr>
                    </thead>
                    <tbody v-for="(user, i) in usersList" :key="i">
                        <tr v-if="user.verified == true">
                            <td scope="row">{{ user.name }}</td>
                            <td>{{ user.email }}</td>
                            <td>{{ user.userType }}</td>
                            <td>{{ user.outstandingFees }}</td>
                            <td>Present</td>
                            <td>
                                <b-dropdown id="dropdown-buttons" text="Change Status" size="sm" variant="primary">
                                    <b-dropdown-item-button v-b-modal.modal-1 @click="sendInfo(user.email)">Enable User
                                    </b-dropdown-item-button>
                                    <b-dropdown-item-button v-b-modal.modal-2 @click="sendInfo(user.email)">Disable User
                                    </b-dropdown-item-button>
                                    <b-dropdown-item-button v-b-modal.modal-3 @click="sendInfo(user.email)">Delete User
                                    </b-dropdown-item-button>
                                </b-dropdown>
                            </td>
                            <td class="text-nowrap">
                                <b-button v-b-modal.modal-4 class="btn-sm" variant="info" @click="sendInfo(user.email)">
                                    Update User Type
                                </b-button>
                            </td>
                        </tr>
                        <tr v-else class="text text-secondary" style="background: #e8e9ec">
                            <td scope="row">{{ user.name }}</td>
                            <td>{{ user.email }}</td>
                            <td>{{ user.userType }}</td>
                            <td>{{ user.outstandingFees }}</td>
                            <td>Disabled</td>
                            <td>
                                <b-dropdown id="dropdown-buttons" text="Change Status" size="sm" variant="primary">
                                    <b-dropdown-item-button v-b-modal.modal-1 @click="sendInfo(user.email)">Enable User
                                    </b-dropdown-item-button>
                                    <b-dropdown-item-button v-b-modal.modal-2 @click="sendInfo(user.email)">Disable User
                                    </b-dropdown-item-button>
                                    <b-dropdown-item-button v-b-modal.modal-3 @click="sendInfo(user.email)">Delete User
                                    </b-dropdown-item-button>
                                </b-dropdown>
                            </td>
                            <td class="text-nowrap">
                                <b-button v-b-modal.modal-4 class="btn-sm" variant="info" @click="sendInfo(user.email)">
                                    Update User Type
                                </b-button>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <b-modal id="modal-1" title="Enable User" alignment="center">
                    <template #modal-footer>
                        <b-button variant="secondary" @click="$bvModal.hide('modal-1')">
                            Cancel</b-button>
                        <b-button variant="primary" @click="enableUser(email); $bvModal.hide('modal-1')">Yes
                        </b-button>
                    </template>
                </b-modal>
                <b-modal id="modal-2" title="Disable User" alignment="center">
                    <template #modal-footer>
                        <b-button variant="secondary" @click="$bvModal.hide('modal-2')">
                            Cancel</b-button>
                        <b-button variant="primary" @click="disableUser(email); $bvModal.hide('modal-2')">Yes
                        </b-button>
                    </template>
                </b-modal>
                <b-modal id="modal-3" title="Delete User" alignment="center">
                    <template #modal-footer>
                        <b-button variant="secondary" @click="$bvModal.hide('modal-3')">
                            Cancel</b-button>
                        <b-button variant="primary" @click="deleteUser(email); $bvModal.hide('modal-3')">Yes
                        </b-button>
                    </template>
                </b-modal>
                <b-modal id="modal-4" title="Update User Type" alignment="center">
                    <div>
                        <b-button class="mx-2 my-2 btn-warning" @click="promoteAdmin(email)">Promote to Admin</b-button>
                        <b-button class="mx-2 my-2 btn-warning" @click="makeGO(email)">Make GO</b-button>
                        <b-button class="mx-2 my-2 btn-warning" @click="demoteStaff(email)">Demote to Staff</b-button>
                    </div>
                    <template #modal-footer>
                        <b-button variant="secondary" @click="$bvModal.hide('modal-4')">
                            Cancel</b-button>
                        <b-button variant="primary" @click="updateUser(email); $bvModal.hide('modal-4')">Yes
                        </b-button>
                    </template>
                </b-modal>
            </div>
        </div>
    </div>
</template>
  
<script>
import axios from "axios";

export default {
    name: "UsersAdmin",
    data() {
        return {
            usersList: [{ name: "", email: "", userType: "", outstandingFees: "", verified: "" }],
            email: '',
            filter: false,
            api: {
                users: "http://localhost:8080/usermanagement/users",
                filterUsers: "http://localhost:8080/usermanagement/usersbytype",
                disableUser: "http://localhost:8080/usermanagement/disable",
                enableUser: "http://localhost:8080/usermanagement/enable",
                deleteUser: "http://localhost:8080/usermanagement/delete",
                updateUser: "http://localhost:8080/usermanagement/delete",
                promoteAdmin: "http://localhost:8080/usermanagement/promote",
                makeGO: "http://localhost:8080/usermanagement/makego",
                demoteStaff: "http://localhost:8080/usermanagement/demote",
            }
        };
    },

    mounted() {
        if (this.filter == false) {
            this.getUsers();
        } else {
            this.filterByType();
        }
    },
    created() {
        this.user = JSON.parse(localStorage.getItem('user'));
    },
    methods: {
        getUsers() {
            return axios
                .get(this.api.users, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                        userEmail: this.userEmail
                    }
                })
                .then((response) => {
                    console.log(response.data);
                    this.usersList = response.data;
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        async filterByType(userType) {
            console.log(userType)
            await axios
                .get(this.api.filterUsers, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                        "userType": userType
                    }
                })
                .then((response) => {
                    console.log(response.data);
                    this.usersList = response.data;
                    this.filter = true;
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        sendInfo(email) {
            this.email = email;
        },

        enableUser(email) {
            return axios
                .put(this.api.enableUser, {
                    "email": email,
                }, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
                })
                .then((response) => {
                    console.log(response.data);
                    this.$alert("User has been enabled successfully").then(() => {
                        window.location.reload();
                    });
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        disableUser(email) {
            return axios
                .put(this.api.disableUser, {
                    "email": email,
                }, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
            })
                .then((response) => {
                    console.log(response.data);
                    this.$alert("User has been disabled successfully").then(() => {
                        window.location.reload();
                    });
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        deleteUser(email) {
            return axios
                .put(this.api.deleteUser, {
                    "email": email,
                }, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
            })
                .then((response) => {
                    console.log(response.data);
                    this.$alert("User has been deleted successfully").then(() => {
                        window.location.reload();
                    });
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        promoteAdmin(email) {
            return axios
                .put(this.api.promoteAdmin, {
                    "email": email,
                }, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
            })
                .then((response) => {
                    console.log(response.data);
                    this.$alert("User has been promoted to Admin successfully").then(() => {
                        window.location.reload();
                    });
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        makeGO(email) {
            return axios
                .put(this.api.makeGO, {
                    "email": email,
                }, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
            })
                .then((response) => {
                    console.log(response.data);
                    this.$alert("User has been promoted to GO successfully").then(() => {
                        window.location.reload();
                    });
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },

        demoteStaff(email) {
            return axios
                .put(this.api.demoteStaff, {
                    "email": email,
                }, {
                    params: {
                        Authorization: "Bearer " + this.user.jwt,
                    }
            })
                .then((response) => {
                    console.log(response.data);
                    this.$alert("User has been demoted to Staff successfully").then(() => {
                        window.location.reload();
                    });
                })
                .catch((error) => {
                    console.log(error.response);
                });
        },
    },
};
</script>

<style>
    .table{
        font-size: 13px;
    }
</style>