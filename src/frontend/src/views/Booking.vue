<template>
<div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-md-7" >
         <CalendarFilter @filter-attraction="filterAttraction" ></CalendarFilter>
         <Calendar v-on:booking-confirmed="forceRerender" :key="this.selected_attraction" :selected_attraction="this.selected_attraction" fluid></Calendar>
        </div>
        <div class="col-md-5" v-if="this.user!='GENERALOFFICE'">
          <Sidebar :key="componentKey" ></Sidebar>
        </div>
      </div>
    </div>
  </div>
 </template>
 
 
 <script>
    import DashboardLayout from '../layouts/DashboardLayout'
    import Calendar from '../components/Booking/Calendar'
    import CalendarFilter from '../components/Booking/CalendarFilter.vue';
    import Sidebar from '../components/Booking/Sidebar.vue';
    
    export default {
            name: "booking-page",
            components: { Calendar, CalendarFilter, Sidebar },
            data(){
              return {
                selected_attraction: null,
                componentKey:0
              }
            },
            
            created() {
                this.$emit('update:layout', DashboardLayout);
                this.user= JSON.parse(localStorage.getItem('user')).userType.authority
            },
            methods: {
              filterAttraction(selected_attraction) {
                this.selected_attraction = selected_attraction;
              },
              forceRerender(){
                this.componentKey+=1
              },
            }
        };
</script>