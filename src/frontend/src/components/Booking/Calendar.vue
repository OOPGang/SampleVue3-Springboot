<script>
    import '@fullcalendar/core/vdom' // solves problem with Vite
    import FullCalendar from '@fullcalendar/vue'
    import dayGridPlugin from '@fullcalendar/daygrid'
    import interactionPlugin from '@fullcalendar/interaction'
    import bootstrap5Plugin from '@fullcalendar/bootstrap5';
    import BookingPopUp from './BookingPopUp.vue';
    import WaitingListPopUp from './WaitingListPopUp.vue';
    import BookingConfirmation from './BookingConfirmation.vue';
    import WaitingListConfirmation from './WaitingListConfirmation.vue';
    import axios from 'axios';

    export default {
        name:'calendar-component',
        components: {
    FullCalendar,
    BookingPopUp,
    WaitingListPopUp,
    BookingConfirmation,
    WaitingListConfirmation
},
      props:['selected_attraction'],
      data() {
        return {
          componentKey:0,
          api: {
            get_avail_passes: "http://localhost:8080/loan/availpasses" ,
            get_passes: 'http://localhost:8080/pass/passes/',
            get_bookings: 'http://localhost:8080/loan/',
          },
          loans_in_month: {},
          curr_bookings: [],
          availabilities: [],
          avail_passes: [],
          selected_date: '',
          selected_no_pass:'',
          calendarOptions: {
            headerToolbar: {
              left: 'prev,next today',
              center: 'title',
              right: 'dayGridMonth'
            },
            plugins: [ dayGridPlugin, interactionPlugin, bootstrap5Plugin],
            selectable: true,
            // themeSystem: 'bootstrap5',
            initialView: 'dayGridMonth',
            views: {
              dayGridMonth: {
                
                // options apply to dayGridMonth, dayGridWeek, and dayGridDay views
              },
              timeGrid: {
                // options apply to timeGridWeek and timeGridDay views
              },
              week: {
                // options apply to dayGridWeek and timeGridWeek views
              },
              day: {
                // options apply to dayGridDay and timeGridDay views
              }},
            dateClick: this.handleDateClick,
            events: [],
            showModal: false
          }
        }
      },
      created() {
        this.getBookings();
        if(this.selected_attraction!= null){
          this.getPasses();
          this.loadData();
        }
    },
    computed:{
      hasExceeededForMonth(date){
        var month = date.substring(0,7)
        if(this.loans_in_month[month]>=2){
          return true
        }
        return false
      }
    },
    methods: {
      async getBookings() {
        await axios
            //change to user email
            .get(this.api.get_bookings + "singaporesportsschooltest@outlook.com")
            .then((response) => {
                // console.log(response.data)
                var loans_in_month= {}
                this.curr_bookings = response.data;
                var unique_ids= [];
                for(var booking of this.curr_bookings){
                  var month = booking.loanDate.substring(0,7)
                  var loanId = booking.loanID
                  if(this.containsKey(loans_in_month, month) && (!unique_ids.includes(loanId))){
                    //alr exists and is unique
                    loans_in_month[month]+= 1
                    unique_ids.push(loanId)
                  }else{
                    loans_in_month[month]= 1 
                }
                this.loans_in_month= loans_in_month
              }
              })
            .catch((error) => {
                console.log(error.response);
            });
        },
      async getPasses(){
        await axios
          .get(this.api.get_passes + this.selected_attraction)
          .then((response) => {
            this.selected_max_avail= response.data.body.length
          })
          .catch((error) => {
              if (error) {
                  console.log(error);
              }
          });
      },
      async loadData(){
        await axios
          .get(this.api.get_avail_passes)
          .then((response) => {
            this.total_loans = response.data
            var result= []
            const today = new Date()
            var tomorrow =  new Date()
            tomorrow.setDate(today.getDate() + 1)
            var last =  new Date()
            last.setDate(today.getDate() + 56)
            var avail_dates = []
            var daysOfYear = [];
            for (var d = tomorrow; d <= last; d.setDate(d.getDate() + 1)) {
                daysOfYear.push(new Date(d));
                var date= new Date(d)
                var dd = String(date.getDate()).padStart(2, '0');
                var mm = String(date.getMonth() + 1).padStart(2, '0'); //January is 0!
                var yyyy = date.getFullYear();
                avail_dates.push(yyyy+"-" +mm+ "-"+ dd)
            }
            var temp;
            if(this.containsKey(this.total_loans,this.selected_attraction)){
              this.availabilities=  response.data[this.selected_attraction]
              for(date of avail_dates){
                if(this.containsKey(this.availabilities,date)){
                  //all available
                  if(this.availabilities[date]!=0){
                    temp= { title: this.availabilities[date]+ ' available', 'date': date,   display: 'background', passes_left: this.availabilities[date]  }
                  }
                }
                //all still available
                else{
                  temp= { title: this.selected_max_avail + ' available', 'date': date,   display: 'background' , passes_left: this.selected_max_avail}
                }
                result.push(temp)
              }
            }else{
              for(date of avail_dates){
                  temp= { title: this.selected_max_avail + ' available', 'date': date,   display: 'background' , passes_left: this.selected_max_avail}

                  result.push(temp)
                }
            }

            this.calendarOptions.events= result;
            console.log("HEKLLLOO THERE")
            console.log(result)
          })
          .catch((error) => {
              if (error) {
                  console.log(error);
              }
          });
        },
        containsKey(obj, key ) {
        return Object.keys(obj).includes(String(key));
        },
        toggleWeekends: function() {
        this.calendarOptions.weekends = !this.calendarOptions.weekends // toggle the boolean!
        },
        forceRerender(){
          this.componentKey+=1
          this.$emit('booking-confirmed')
        },
        handleDateClick: function(info){ 
          let availability=  info.dayEl.innerText.split("\n")[1];
          if (availability.includes('available')){
            this.selected_date = info.dateStr;
            if(this.containsKey(this.availabilities, info.dateStr)){
              this.selected_no_pass= this.availabilities[info.dateStr];
            }else{
              this.selected_no_pass= this.selected_max_avail;
            }
            this.$root.$refs.BookingPopUp.showModal();

          }else if (availability== 'unavailable'){
            this.$root.$refs.WaitingListPopUp.showModal(info.dateStr);
          }
        }
      }
    }
    </script>
    <template>
      <div>
        <FullCalendar :key="componentKey" :options="calendarOptions" />
        <BookingPopUp :key="this.selected_date" :selected_date='this.selected_date' :no_avail="this.selected_no_pass" :attraction_name="this.selected_attraction" ></BookingPopUp>
        <WaitingListPopUp></WaitingListPopUp>
        <BookingConfirmation v-on:booking-confirmed="forceRerender"></BookingConfirmation>
        <WaitingListConfirmation></WaitingListConfirmation>   
      </div>
    </template>