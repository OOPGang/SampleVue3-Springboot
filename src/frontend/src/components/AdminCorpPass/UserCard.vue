<template>
  <card>
    <h4 slot="header" class="card-title"><strong>Create New Passes</strong></h4>
    <form>  
      <div class="form-row">
        <div  class="col-md-6">
          <label>Corporate Pass</label>
          <b-form-select 
            v-model="selected_attraction" 
            :options="attraction_choices"
            required
          ></b-form-select>
        </div>
        <div class="col-md-6">
          <base-input type="number"
                    v-model= "no_passes"
                    label="No. of Cards"
                    min="1"
                    max="10"
                    placeholder="1"
                    >
          </base-input> 
          </div>
      </div>

      <div class="form-row">
          <div class="col-md-12">
              <label class="mt-0 d-block">Corporate Pass Numbers</label> 
              <small class="text-danger" v-if="!areDistinct">All pass numbers must be unique.</small>
              <div  v-for="index in parseInt(no_passes)" :key="index">
                <b-form-input
                  v-model= corporate_passes[index-1]
                  class="form-row mb-1"
                  type="text"
                  placeholder="Enter Corporate Pass Number"
                  size="sm"
                  required
                  ></b-form-input>
                  <small class="text-danger" v-if="!isPassNoUnique(corporate_passes[index-1]) && corporate_passes[index-1]!=null">Pass Number already exists</small>
                </div>
          </div>
      </div>

      <div class="text-center">
        <b-button  type="submit" variant="primary" class="float-right" @click.prevent="createPass" :disabled="!isFormValid">
          Create Passes
        </b-button>  
      </div>
      <div class="clearfix"></div>

      <div>
        <PassCreationConfirmation></PassCreationConfirmation>
      </div>
    </form>
  </card>
  
</template>
<script>
  import Card from '../Cards/Card.vue'
  import PassCreationConfirmation from './PassCreationConfirmation.vue';
  import axios from 'axios';

  export default {
    components: {
      Card,
      PassCreationConfirmation
    },
    data () {
      return {
        no_passes: 1,
        current_attractions: [],
        current_passes: [],
        passes_in_attraction: {null:[]},
        attraction_choices: [{ value: "", text: 'Select Corporate Pass' }],
        selected_attraction: "",
        corporate_passes: [],
        all_passes_unique: true,
        api: {
            create_pass: "http://localhost:8080/passmanagement/new",
            get_passes: "http://localhost:8080/pass/passes",
            get_attractions: "http://localhost:8080/attraction/attractions"
          },
        error_msg: {
          valid_pass_no: ""
        }
      }
    },
    watch:{
      no_passes(){
        if(this.no_passes>10){
          this.no_passes=10;
        }
      },
      corporate_passes(){
        console.log(this.corporate_passes)
        this.all_passes_unique= true;
        for(var passNo of this.corporate_passes){
          if(!this.isPassNoUnique(passNo)){
            this.all_passes_unique=false;
          }
        }       
      }
    },
    computed: {
      isFormValid(){
        return this.corporate_passes.length>0 && this.selected_attraction!=null && this.all_passes_unique && this.areDistinct
      },
      areDistinct()
      {
          var arr= this.corporate_passes
          let n = arr.length;
      
          // Put all array elements in a map
          let s = new Set();
          for (let i = 0; i < n; i++) {
              s.add(arr[i]);
          }
      
          // If all elements are distinct, size of
          // set should be same array.
          return (s.size == arr.length);
      },
    },
    methods: {
      isPassNoUnique(passNo){
        if(this.corporate_passes.length){
          return !(this.passes_in_attraction[this.selected_attraction].includes(passNo))
        }
      },
      formReset(){
        this.no_passes=1
        this.corporate_passes=[]
        this.selected_attraction= ""
      },
      async createPass () {
        if(this.isFormValid){
          for(var passNo of this.corporate_passes){
            await axios 
            .post(this.api.create_pass, 
              {
                "passNo":passNo,
                "attractionName": this.selected_attraction,
                "passStatus": "INOFFICE"
              }
              )
            .then((response) => {
              console.log(response);
            }); 
          }
          this.$root.$refs.PassCreationConfirmation.showModal(this.corporate_passes.length, this.selected_attraction);
          this.formReset()
          this.loadData()
        }
      },
      async loadData(){
        await axios
          .get(this.api.get_passes)
          .then((response) => {
            this.current_passes = response.data
          })
          .catch((error) => {
              if (error) {
                  console.log(error);
              }
          });
          
          await axios
          .get(this.api.get_attractions)
          .then((response) => {
            this.current_attractions = response.data
          })
          .catch((error) => {
              if (error) {
                  console.log(error);
              }
          });

        for(var attraction of this.current_attractions){
          this.attraction_choices.push(
            {
              text: attraction["attractionName"],
              value: attraction["attractionName"]
          }
          )
          this.passes_in_attraction[attraction["attractionName"]]= []
        }

        for(var pass of this.current_passes){
          var pass_no = pass["passNo"]
          var attraction_name= pass["attractionName"]
          this.passes_in_attraction[attraction_name].push(pass_no)
          
        }
      }
    },
    mounted(){
      this.loadData();
    }
  }

</script>
<style>

</style>
