<template>
  <div class="table-container">
    <!-- User Interface controls -->
          <b-row>
        <b-col lg="6" class="my-1">
          <b-form-group
            label="Sort"
            label-for="sort-by-select"
            label-cols-sm="1"
            label-align-sm="left"
            label-size="sm"
            class="mb-0"
            v-slot="{ ariaDescribedby }"
          >
            <b-input-group size="sm">
              <b-form-select
                id="sort-by-select"
                v-model="sortBy"
                :options="sortOptions"
                :aria-describedby="ariaDescribedby"
                class="w-75"
              >
                <template #first>
                  <option value="">-- none --</option>
                </template>
              </b-form-select>
  
              <b-form-select
                v-model="sortDesc"
                :disabled="!sortBy"
                :aria-describedby="ariaDescribedby"
                size="sm"
                class="w-25"
              >
                <option :value="false">Asc</option>
                <option :value="true">Desc</option>
              </b-form-select>
            </b-input-group>
          </b-form-group>
        </b-col>
  
        <b-col lg="4" class="my-1 mx-5">
          <b-button variant="success" @click="handleAdd()">Create New Pass</b-button>
        </b-col>
  
        <b-col lg="6" class="my-1">
          <b-form-group
            label="Filter"
            label-for="filter-input"
            label-cols-sm="1"
            label-align-sm="left"
            label-size="sm"
            class="mb-0"
          >
            <b-input-group size="sm">
              <b-form-input
                id="filter-input"
                v-model="filter"
                type="search"
                placeholder="Type to Search"
              ></b-form-input>
  
              <b-input-group-append>
                <b-button :disabled="!filter" @click="filter = ''">Clear</b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </b-col>
  
        <b-col lg="6" class="my-1">
          <b-form-group
            v-model="sortDirection"
            label="Filter On"
            description="Leave all unchecked to filter on all data"
            label-cols-sm="3"
            label-align-sm="right"
            label-size="sm"
            class="mb-0"
            v-slot="{ ariaDescribedby }"
          >
            <b-form-checkbox-group
              v-model="filterOn"
              :aria-describedby="ariaDescribedby"
              class="mt-1"
            >
              <b-form-checkbox value="attractionName">Name</b-form-checkbox>
              <b-form-checkbox value="passNo">Pass Number</b-form-checkbox>
              <b-form-checkbox value="passStatus">Pass Status</b-form-checkbox>
            </b-form-checkbox-group>
          </b-form-group>
        </b-col>
  
        <b-col sm="5" md="6" class="my-1">
          <b-form-group
            label="Per page"
            label-for="per-page-select"
            label-cols-sm="6"
            label-cols-md="4"
            label-cols-lg="2"
            label-align-sm="left"
            label-size="sm"
            class="mb-0"
          >
            <b-form-select
              id="per-page-select"
              v-model="perPage"
              :options="pageOptions"
              size="sm"
            ></b-form-select>
          </b-form-group>
        </b-col>
  
        <b-col sm="7" md="6" class="my-1">
          <b-pagination
            v-model="currentPage"
            :total-rows="totalRows"
            :per-page="perPage"
            align="fill"
            size="sm"
            class="my-0"
          ></b-pagination>
        </b-col>
      </b-row>
    <b-editable-table
      head-variant="dark"
      disableDefaultEdit
      :rowUpdate="rowUpdate"
      :editMode="'row'"
      bordered
      class="editable-table"
      v-model="items"
      :fields="fields"
      :current-page="currentPage"
      :per-page="perPage"
      :filter="filter"
      :filter-included-fields="filterOn"
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDesc"
      :sort-direction="sortDirection"
      stacked="md"
      show-empty
      small
      @filtered="onFiltered"
      @input-change="handleInput"
    >
      <template #cell(isActive)="data">
        <span v-if="data.value">Yes</span>
        <span v-else>No</span>
      </template>
      <template #cell(edit)="data" >
        <div v-if="data.isEdit">
          <BIconX class="edit-icon" @click="handleSubmit(data, false)"></BIconX>
          <BIconCheck
            class="edit-icon"
            @click="handleSubmit(data, true)"
          ></BIconCheck>
        </div>
        <BIconPencil
          v-else
          class="edit-icon"
          @click="handleEdit(data, true)"
        ></BIconPencil>
      </template>
      <template #cell(activate)="data" >
        <div>
          <b-button v-if="data.item.passStatus == 'INOFFICE'" @click="showPopup('Deactivation', data.item.passId)" variant="danger">Deactivate</b-button> 
          <b-button v-if="data.item.passStatus == 'DEACTIVATED'" @click="showPopup('Reactivation', data.item.passId)" variant="success">Reactivate</b-button>
        </div>
      </template>
    </b-editable-table>
    <div hidden>
        <StatusChangePopUp :action="this.action" :pass="this.pass_to_upate" v-on:update-status="handleUpdate"></StatusChangePopUp>
    </div>
  </div>


</template>

<script>
import axios from 'axios'
import BEditableTable from "bootstrap-vue-editable-table";
import   StatusChangePopUp from './StatusChangePopUp.vue';
import {
  BIconTrash,
  BIconPencil,
  BIconX,
  BIconCheck,
  BButton,
} from "bootstrap-vue";
export default {
  components: {
    BEditableTable,
    BIconX,
    BIconTrash,
    BIconPencil,
    BIconCheck,
    BButton,
    StatusChangePopUp,
  },
  data() {
    return {
      action: '',
      curr_passno_list: [],
      pass_to_upate:  '',
      placeOfInterest: '',
      api: { view_passes: "http://localhost:8080/pass/passes/",
            activate_pass: "http://localhost:8080/passmanagement/activate",
            deactivate_pass: "http://localhost:8080/passmanagement/deactivate",
            edit_pass: "http://localhost:8080/passmanagement/",
            create_pass: "http://localhost:8080/passmanagement/new",
    },
      items: null,
      updatedRow: {},
      fields: [
        { key: "edit", label: "",  class:"edit-col"  },
        {
          key: "passId",
          label: "Pass Id",
          type: "text",
          editable: false,
          placeholder: "Enter Name...",
          class: "id-col",
          sortable: true,
        },
        {
          key: "passNo",
          label: "Pass No.",
          type: "text",
          editable: true,
          class: "pass-no-col",
          validate: this.validateNo,
          sortable: true,
        },
        {
          key: "passStatus",
          label: "Pass Status",
          type: "select",
          editable: false,
          class: "status-col",
          sortable: true,
          options: [
            { value: "INOFFICE", text: "INOFFICE" },
            { value: "DEACTIVATED", text: "DEACTIVATED" },
          ],
        },
        { key: "activate", label: "" },
      ],
      rowUpdate: {},
      totalRows: 1,
      currentPage: 1,
      perPage: 5,
      pageOptions: [5, 10, 15, { value: 100, text: "Show a lot" }],
      sortBy: '',
      sortDesc: false,
      sortDirection: 'asc',
      filter: null,
      filterOn: [],
    };
  },
  methods: { 
    async handleUpdate() {
      await this.action=="Reactivation"? this.handleActivate(this.pass_to_upate) : this.handleDeactivate(this.pass_to_upate)            
      },
    handleInput(data) {
      if (Object.keys(this.updatedRow).length === 0) {
        this.updatedRow = {
          ...this.items[data.index],
          [data.field.key]: data.value,
        };
      } else {
        this.updatedRow = { ...this.updatedRow, [data.field.key]: data.value };
      }
    },
    handleAdd() {
      const newId = Date.now();
      this.rowUpdate = {
        edit: true,
        id: newId,
        action: "add",
        data: {
          action: 'add',
          id: newId,
          passId: '',
          passNo: '',
          passStatus:'INOFFICE'
        },
      };
    },
    async handleSubmit(data, update) {
      if(data.item.action=='add'){
        await axios 
            .post(this.api.create_pass, 
              {
                "passNo" : this.updatedRow.passNo,
                "attractionName": this.placeOfInterest,
                "passStatus": "INOFFICE"
              }
              )
            .then((response) => {
              console.log(response);
              data.item.passId= this.placeOfInterest+  this.updatedRow.passNo
            }); 
      }else{
        await axios 
            .put(this.api.edit_pass + this.updatedRow.passId + '/edit' , 
              {
                "passNo" : this.updatedRow.passNo
              }
              )
            .then((response) => {
              console.log(response);
            }); 
      }
      this.rowUpdate = {
            edit: false,
            id: data.id,
            action: update ? "update" : "cancel",
          };
          
        this.updatedRow = {}
    },
    handleEdit(data) {
      console.log(this.items)
      this.rowUpdate = { edit: true, id: data.id };
    },
    handleDelete(data) {
      this.rowUpdate = { id: data.id, action: "delete" };
    },
    showPopup(action, data){
      this.action= action;
      this.pass_to_upate= data;
      this.$root.$refs.StatusChangePopUp.showModal();
    },
    async handleActivate(data) {
      await axios 
      .put(this.api.activate_pass, 
        {
          "passId" : data    
        }
        )
      .then((response) => {
        console.log(response);
        this.$emit('update-status');

      }); 
     
    },
    async handleDeactivate(data) {
      await axios 
            .put(this.api.deactivate_pass, 
              {
                "passId" : data    
              }
              )
            .then((response) => {
              console.log(response);
              this.$emit('update-status');
            }); 
    },
    async viewPass(placeofinterest){
          this.placeOfInterest= placeofinterest;
          var view_pass_api= this.api.view_passes + this.placeOfInterest
          await axios
            .get(view_pass_api)
            .then((response) => {
                var passes_list = response.data.body;
                var curr_passno_list= [];
                console.log(passes_list)
                passes_list.forEach(function (value, i) {
                    curr_passno_list.push(value.passNo)
                    value["id"]= i+1
                });
                this.items= passes_list
                this.curr_passno_list = curr_passno_list 
                console.log(this.items)
            })
            .catch((error) => {
                console.log(error);
            })
          this.totalRows = this.items.length
      },
    validateNo(value) {
      
        if (this.curr_passno_list.includes(value)) {
          return {
            valid: false,
            errorMessage: 'Current pass number exists. Please set a unique number.'
          }
        }
        return {valid: true};
      },
      info(item, index, button){
        this.infoModal.title= `Row index: ${index}`
        this.infoModal.content = JSON.stringify(item, null, 2)
        this.$root.$emit('bv::show::modal', this.infoModal.id,button)
      },
      resetInfoModal() {
        this.infoModal.title = ''
        this.infoModal.content = ''
      },
      onFiltered(filteredItems) {
        // Trigger pagination to update the number of buttons/pages due to filtering
        this.totalRows = filteredItems.length
        this.currentPage = 1
      },
  },
  computed:{
      sortOptions() {
        // Create an options list from our fields
        return this.fields
          .filter(f => f.sortable)
          .map(f => {
            return { text: f.label, value: f.key }
          })
      },
  },
  created(){
    this.$root.$refs.PassDataTable= this;
    this.viewPass(this.$route.params.name)
  },
  beforeRouteUpdate(to, from, next) {
    // Call the API query method when the URL changes
    this.viewPass(to.params.name)
    next()
  }

};
</script>

<style>
.table-container {
  margin: 10px;
}

table.editable-table {
  margin-top: 10px;
}

table.editable-table td {
  vertical-align: middle;
}

.editable-table .data-cell {
  padding: 8px;
  vertical-align: middle;
}

.editable-table .custom-checkbox {
  width: 50px;
}

.remove-icon {
  color: red;
  cursor: pointer;
  font-size: 20px;
}

.edit-icon {
  color: rgb(4, 83, 158);
  cursor: pointer;
  font-size: 20px;
}

.id-col {
  width:  15%;
}

.pass-no-col {
  width: 30%;
}


.status-col {
  width:  20%;
}

</style>