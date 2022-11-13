<template>
    <card>
      <h4 slot="header" class="card-title"><strong>Create New Attraction</strong></h4>
      <form enctype="multipart/form-data">  
        <div class="row">
          <div class="col-md-12">
            <base-input type="text"
                      class="mb-0"
                      label="Attraction Name"
                      placeholder="eg. Singapore Gardens"
                      v-model="attractionname"
                      >
            </base-input>
            <small class="text-danger" v-if="!isUniqueName">Attraction name exists. Try another name.</small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-6">
            <label>Pass Replacement Fee ($)</label>
            <b-input-group prepend="$">
                <b-form-input
                    type="number"
                    min="0"
                    v-model="replacementfee"
                ></b-form-input> 
            </b-input-group>
          </div>
          <div class="col-md-6">
            <label>Pass Type</label>
            <b-form-radio-group
                id="radio-group-1"
                v-model="selected_passtype"
                :options="options"
                name="radio-options"
            ></b-form-radio-group>
          </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <label>Templates</label>
                <b-form-file
                enctype="multipart/form-data"
                v-model="email_file"
                :state="Boolean(email_file)"
                placeholder="Upload Email Template Here..."
                drop-placeholder="Drop file here..."
                ></b-form-file>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <b-form-file
                enctype="multipart/form-data"
                v-model="pdf_file"
                :state="Boolean(pdf_file)"
                placeholder="Upload PDF Attachement Template Here..."
                drop-placeholder="Drop file here..."
                ></b-form-file>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <label>Pass Image</label>
                <b-form-file
                enctype="multipart/form-data"
                v-model="image"
                accept=".jpg, .jpeg, .png"
                :state="Boolean(image)"
                placeholder="Upload Photo Here..."
                drop-placeholder="Drop file here..."
                ></b-form-file>
            </div>
        </div>

        <div class="text-center">
          <b-button type="submit" variant="primary" class="float-right" @click.prevent="createAttraction" :disabled="!isFormValid">
            Create Attraction
          </b-button>
        </div>
        <div class="clearfix"></div>
        <div>
        <AttractionCreationConfirmation></AttractionCreationConfirmation>
      </div>
      </form>
    </card>
  </template>
  <script>
    import Card from '../Cards/Card.vue'
    import AttractionCreationConfirmation from './AttractionCreationConfirmation.vue'
    import axios from 'axios';

    export default {
      components: {
        Card,
        AttractionCreationConfirmation
      },
      data () {
        return {
          //TODO: Replace with data
          attractionname: '',
          replacementfee: 0,
          email_file: null,
          pdf_file: null,
          image: null,
          email_file_name: null,
          pdf_file_name: null,
          image_name: null,
          selected_passtype: '',
          current_attractions: [],
          attraction_names: [],
          options: [
          { text: 'Physical Card', value: 'PHYSICALPASS' },
          { text: 'E-card', value: 'ELECTRONICPASS' }
          ],
          api: {
            create_attraction: "http://localhost:8080/attractionmanagement/new" ,
            upload_files:  "",
            upload_image: "",
            get_attractions: "http://localhost:8080/attraction/attractions"
          },
        }
      },
      watch: {
        attractionname() {
            this.api.upload_files =  "http://localhost:8080/attractionmanagement/" + this.attractionname + "/uploadfiles"
            this.api.upload_image =  "http://localhost:8080/attractionmanagement/" + this.attractionname + "/uploadimage"
        },
      },
      computed:{
        isFormValid(){
          return this.attractionname!= '' &&
            this.isUniqueName &&
            this.replacementfee!= -1 &&
            this.email_file!= null &&
            this.pdf_file!= null &&
            this.image!= null &&
            this.selected_passtype!= ''
          },
        isUniqueName(){
          return !this.attraction_names.includes(this.attractionname)
        },
      },
      async mounted(){
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
          this.attraction_names.push(attraction["attractionName"])
        }
      },
      methods: {
        formReset(){
          this.attractionname= ''
          this.replacementfee= 0
          this.email_file= null
          this.pdf_file= null,
          this.image= null,
          this.selected_passtype= ''
        },
        async createAttraction () {
          if(this.isFormValid){

            //1. create attraction details
            await axios 
            .post(this.api.create_attraction, 
              {
                "attraction" : this.attractionname,
                "replacementFee" : parseFloat(this.replacementfee),
                "passType" : this.selected_passtype
              }
              )
            .then((response) => {
              console.log(response);
            }); 

            //2. upload templates
            let filesData = new FormData();
            filesData.append('emailtemplate', this.email_file);
            filesData.append('attachment', this.pdf_file);

            await axios 
            .put(this.api.upload_files, 
            filesData,
              {
                headers: {
                'Content-Type': 'multipart/formdata'      
              }
            }
              )
            .then((response) => {
              console.log(response);
            }); 
                       
            //3. upload image
            let imageData = new FormData();
            imageData.append('image', this.image);

            await axios 
            .put(this.api.upload_image, 
               imageData,
              {
                headers: {
                'Content-Type': 'multipart/formdata'      
              }
              }
              )
            .then((response) => {
              console.log(response);
            }); 

            this.$root.$refs.AttractionCreationConfirmation.showModal(this.attractionname);
          }
          this.formReset()
        },

      }
    }
  
  </script>
  <style>
  
  </style>
  