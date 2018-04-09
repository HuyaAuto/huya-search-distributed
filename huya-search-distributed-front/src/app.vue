<style scoped>
    @import 'styles/common.css';
    .layout{
        height: 100%;
        border: 1px solid #d7dde4;
        background: #f5f7f9;
        position: relative;
        border-radius: 4px;
        overflow: hidden;
    }
    .layout-breadcrumb{
        padding: 10px 15px 0;
    }
    .layout-content{
        height: 85%;
        margin: 15px;
        overflow: hidden;
        background: #fff;
        border-radius: 4px;
    }
    .layout-content-main{
        height: 100%;
        padding: 10px;
    }
    .layout-copy{
        text-align: center;
        padding: 10px 0 20px;
        color: #9ea7b4;
    }
    .layout-menu-left{
        background: #464c5b;
    }
    .layout-header{
        height: 60px;
        background: #fff;
        box-shadow: 0 1px 1px rgba(0,0,0,.1);
    }
    .layout-logo-left{
        width: 90%;
        height: 30px;
        background: #5b6270;
        border-radius: 3px;
        margin: 15px auto;
    }
    .layout-ceiling-main a{
        color: #9ba7b5;
    }
    .layout-hide-text .layout-text{
        display: none;
    }
    .ivu-col{
        transition: width .2s ease-in-out;
    }
    .ivu-row-flex{
        height: 100%;
    }
    .tab {
        margin-right:2em
    }
</style>
<template>
    <div class="layout" :class="{'layout-hide-text': spanLeft < 5}">
        <Row type="flex">
            <i-col :span="spanLeft" class="layout-menu-left">
                <Menu :active-name="routerNum" theme="dark" width="auto" @on-select="select">
                    <div class="layout-logo-left"></div>
                    <Menu-item name="1">
                        <Icon type="podium" :size="iconSize"></Icon>
                        <span class="layout-text">数据查询</span>
                    </Menu-item>
                </Menu>
            </i-col>
            <i-col :span="spanRight">
                <div class="layout-header">
                    <i-button type="text" @click="toggleClick">
                        <Icon type="navicon" size="32"></Icon>
                    </i-button>

                    <Radio-group v-model="env" @on-change="changeEnv">
                        <Radio label="pro">
                            <span>正式环境</span>
                        </Radio>
                        <Radio label="local">
                            <span>本地环境</span>
                        </Radio>
                    </Radio-group>
                </div>
                <div class="layout-breadcrumb">
                </div>
                <div class="layout-content" style="overflow-y:auto;overflow-x:auto;">
                    <router-view>
                    </router-view>
                </div>
                <div class="layout-copy">
                    2011-2016 &copy; TalkingData
                </div>
            </i-col>
        </Row>
    </div>
</template>
<script>
    import Vue from "vue";
    import VCharts from 'v-charts';
    export default {
        data () {
            return {
                env: 'pro',
                routerNum: "",
                spanLeft: 2,
                spanRight: 22
            }
        },
        computed: {
            iconSize () {
                return 24;
            }
        },
        mounted () {
            this.changeEnv(this.env);
        },
        beforeDestroy () {

        },
        methods: {
            toggleClick () {
                // if (this.spanLeft === 5) {
                //     this.spanLeft = 2;
                //     this.spanRight = 22;
                // } else {
                //     this.spanLeft = 5;
                //     this.spanRight = 19;
                // }
            },
            changeEnv (label) {
                switch (label) {
                    case "pro" : Vue.prototype.ajaxApiUrl = "http://14.29.58.111:28888/"; break;
                    case "local" : Vue.prototype.ajaxApiUrl = "http://localhost:28888/"; break;
                    default :
                        Vue.prototype.ajaxApiUrl = "http://localhost:28888/";
                        label = "local";
                }
                this.env = label;
                localStorage.setItem("env", label);
            },
            select (num) {
                switch (num) {
                    case "1" : this.$router.push("/");break;
                }
            },
        }
    }
</script>
