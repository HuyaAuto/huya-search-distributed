<style scoped lang="less" type="text/less">
    .index{
        h1{
            height: 150px;
            img{
                height: 100%;
            }
        }
        h2{
            color: #666;
            margin-bottom: 200px;
            p{
                margin: 0 0 50px;
            }
        }
        .ivu-row-flex{
            height: 100%;
        }
    }
    ::-webkit-scrollbar{
        width: 0px;
    }
    .query{
        position: relative;
    }
</style>
<template>
    <div class="index">
        <Row type="flex">
            <Col span="24">
            <Shard v-on:shard="shard"></Shard>
            </Col >
            <Col span="24">
            <DataTable
                    v-bind:columns="columns"
                    v-bind:data="data"
            ></DataTable>
            </Col >
        </Row>
    </div>
</template>
<script>
    import qs from 'qs';
    import Shard from './shard.vue';
    import DataTable from './dataTable.vue';
    export default {
        components:{
            'Shard': Shard,
            'DataTable': DataTable
        },
        data () {
            return {
                sqlString: "",
                columns: [],
                data: [],
                refresh: false,
                spinShow: false,
                queryInfo: ""
            }
        },
        methods: {
            query (sql) {
                const self = this;
                const notice = this.$Notice;
                console.log(sql);
                this.spinShow = true;
                this.$http.post(this.ajaxApiUrl + "insert/insertStat", sql, {
                    headers: { 'Content-Type': 'text/plain' }
                })

                    .then(function (response) {
                        notice.success({
                            title: '查询成功',
                        });
                        self.spinShow = false;
                        const data = response.data;
                        self.refreshData(data);

                    })
                    .catch(function (error) {
                        notice.success({
                            title: '查询失败',
                            desc: error
                        });
                        self.spinShow = false;

                    });

            },
            refreshData (obj) {
                this.queryInfo = "查询耗时：" + obj.runtime + " ms";
                let columnMap = new Map();
                const data    = obj.data;
                console.log(data);

                const arr = [];
                for (let i = 0; i < data.length; i++) {
                    const row = data[i];
                    const instance = [];
                    for (let columnName in row) {
                        if (row.hasOwnProperty(columnName)) {
                            if (!columnMap.has(columnName)) {
                                let num = columnMap.size;
                                instance[columnMap.size] = this.convert(columnName, row[columnName]);
                                columnMap.set(columnName, num);
                            }
                            else {
                                instance[columnMap.get(columnName)] = this.convert(columnName, row[columnName]);
                            }
                        }
                    }
                    console.log(instance);
                    arr[i] = instance;
                }

                let temp = [];
                columnMap.forEach(function (value, key, map) {
                    temp[value] = key;
                });
                this.columns = temp;
                this.data = arr;
            },
            convert (key, value) {
                if (key === "_area" || key === "_isp") {
                    return decodeURIComponent(value);
                }
                return value;
            }
        }
    }
</script>