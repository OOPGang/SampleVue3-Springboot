<template>
  <div class="mx-4 my-4">
    <transition name="fade" mode="out-in">
    <router-view></router-view>
    </transition>

  </div>

</template>
<script>
  export default {
    name: 'content-layout'
  }
</script>
<style>
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity .1s
  }

  .fade-enter,
  .fade-leave-to
    /* .fade-leave-active in <2.1.8 */

  {
    opacity: 0
  }
</style>
