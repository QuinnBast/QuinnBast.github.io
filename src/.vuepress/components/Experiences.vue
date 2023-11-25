<template>
    <div class="full-width">
        <h1 align="center">My Experiences</h1>
        <b-nav class="p-4" pills align="center" variant="dark">
            <b-nav-item :active="experience == 'Professional'" @click="onClickProfessional">Professional</b-nav-item>
            <b-nav-item :active="experience == 'Personal'" @click="onClickPersonal">Personal</b-nav-item>
        </b-nav>

        <Timeline v-if="this.experience == 'Professional'">
            <TimelineElementLeft title="July 2020 - Present">
                <p><h5 style="display: inline-block">Calian</h5> - Cloud Native Devops Engineer</p>
                <ul>
                    <li>Kubernetes Administrator</li>
                    <ul>
                        <li>Was the go-to person for anything Kubernetes</li>
                        <li>ArgoCD, K8s Operators, Helm, Kustomize...</li>
                        <li>Ingress, nginx, TLS, and various CNIs</li>
                        <li>Creation of custom Helm charts</li>
                    </ul>
                    <li>Focus on creating highly avaliable software solutions</li>
                    <ul>
                        <li>Redundancy</li>
                        <li>Fault Tolerance</li>
                        <li>Replication</li>
                    </ul>
                    <li>Leader of multiple Guilds</li>
                    <ul>
                        <li>Lead the Container Orchestration and Databases guilds</li>
                        <li>Provided regular training sessions and support</li>
                        <li>Set best practices for various tools and technologies</li>
                        <li>Provided architecture recommendations</li>
                    </ul>
                    <li>Devops and infrastructure administrator</li>
                    <li>Developed satellite software in Kotlin and Java</li>
                    <li>Minor development in React</li>
                </ul>
                <template #side>
                    <div class="p-3"><img src="https://upload.wikimedia.org/wikipedia/commons/3/39/Kubernetes_logo_without_workmark.svg" style="height: 120px"></div>
                    <div class="p-3"><img style="width: 50%"/><img src="https://helm.sh/img/helm.svg" style="height: 120px;"></div>
                    <div class="p-3"><img src="https://www.opsmx.com/wp-content/uploads/2022/07/Argo-1-e1630327305635-1.png" style="height: 120px;"></div>
                    <div class="p-3"><img style="width: 50%"/><img src="https://cdn4.iconfinder.com/data/icons/logos-and-brands/512/97_Docker_logo_logos-512.png" style="height: 120px;"></div>
                </template>
            </TimelineElementLeft>
            <TimelineElementRight title="May 1029 - August 2018">
                <p><h5 style="display: inline-block">Sasktel</h5> - Engineering Manager</p>

                <ul>
                    <li>GIS Engineer</li>
                    <ul>
                        <li>Created Geospatial webmaps using Esri products</li>
                        <li>Developer Python scripts and Geolocators</li>
                    </ul>
                    <li>Trained others on Esri's Python and Javascript APIs</li>
                    <li>Querying Oracle Database for Geospatial data</li>
                </ul>

                <template #side>
                    <img src="https://seeklogo.com/images/E/esri-logo-219B143C69-seeklogo.com.png" style="height: 120px;">
                </template>
            </TimelineElementRight>
            <TimelineElementLeft title="May 2018 - August 2018">
                <p><h5 style="display: inline-block">Sasktel</h5> - Engineering Assistant</p>
                <ul>
                    <li>Developed Python geospatial scripts</li>
                    <li>ArcMap, ArcGIS Online, etc.</li>
                    <li>Migrated Microsoft Access Databases</li>
                </ul>

                <template #side>
                    <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Python-logo-notext.svg/115px-Python-logo-notext.svg.png" style="height: 120px;">
                </template>
            </TimelineElementLeft>
            <TimelineElementRight title="September 2017 - December 2017">
                <p><h5 style="display: inline-block">Evraz North America</h5> - Programmer Analyst</p>

                <ul>
                    <li>SQL querying and reporting</li>
                    <ul>
                        <li>Migrated outdated DTS reports to SSIS</li>
                        <li>Windowws Server and SQL Server Management Studio</li>
                    </ul>
                    <li>Minor C# exposure</li>
                </ul>

                <template #side>
                    <img src="https://www.svgrepo.com/show/303229/microsoft-sql-server-logo.svg" style="height: 120px;">
                </template>
            </TimelineElementRight>
        </Timeline>

        <Timeline v-if="this.experience == 'Personal'">
            <TimelineElementRight title="March 2023 - Present">
                <p><h5 style="display: inline-block">Push</h5></p>
                <ul>
                    <li>Puzzle game developed in GameMaker</li>
                    <li>Planning to release to Steam</li>
                </ul>

                <template #side>
                    <img src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/gamemaker/gamemaker.png" style="height: 120px;">
                </template>
            </TimelineElementRight>
            <TimelineElementLeft title="November 2023">
                <h5 style="display: inline-block">This Website</h5>

                <ul>
                    <li>Created this website using Vue.js</li>
                    <li>Hosted through Github pages</li>
                </ul>

                <template #side>
                    <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Vue.js_Logo_2.svg/512px-Vue.js_Logo_2.svg.png" style="height: 120px;">
                </template>
            </TimelineElementLeft>
            <TimelineElementRight title="August 2019 - November 2023">
                <p><h5 style="display: inline-block"><a href="https://github.com/Subterfuge-Revived">Subterfuge Remake</a></h5> - Project Lead</p>

                <p>Revival project for an old app called <a href="http://subterfuge-game.com/">Subterfuge</a>.
                This project was a <b>Unity</b> application with an <b>ASP.NET</b> backend with a <b>MongoDB</b> database.
                The apps used a shared C# dll to allow Unity and the web backend to be able to share core logic.
                This application was close to completion until a benefactor bought out the original application to keep it's servers alive and continue development.</p>

                <template #side>
                    <img src="https://is1-ssl.mzstatic.com/image/thumb/Purple111/v4/17/22/1e/17221e39-3d64-6a07-fd45-7d2f1d573147/mzl.noavmpso.png/230x0w.webp" style="height: 120px;">
                </template>
            </TimelineElementRight>
            <TimelineElementLeft title="April 2019">
                <h5 style="display: inline-block"><a href="https://github.com/QuinnBast/SaskTel-Communication-Portal">Sasktel Communication Portal</a></h5>

                <p>My University 4th year project created in collaboration with <a href="https://www.sasktel.com/">Sasktel.</a> Integrates <b>Web RTC sockets</b> to allow making phone calls from the web.</p>
                <template #side>
                    <img src="https://upload.wikimedia.org/wikipedia/commons/e/e1/SaskTel_logo.svg" style="height: 120px;">
                </template>
            </TimelineElementLeft>
            <TimelineElementRight title="August 2018">
                <p><h5 style="display: inline-block"><a href="https://github.com/QuinnBast/CueB-Game-Engine">CueB Game Engine</a></h5></p>

                <p>A custom 2D game engine written in Java that was inspired by Game Maker</p>
            </TimelineElementRight>
            <TimelineElementLeft title="2010-2013">
                <h5 style="display: inline-block"><a href="https://github.com/QuinnBast/GameMaker-Games-2010-2013">Game Maker Games</a></h5>

                <p>A collection of games I made in high-school using Game Maker. This was my first experinces with coding and I fell in love with it since.</p>
                <template #side>
                    <img src="https://www.svgrepo.com/show/373617/gamemaker.svg" style="height: 120px;">
                </template>
            </TimelineElementLeft>
        </Timeline>
    </div>
</template>

<script>
import Timeline from "./timeline/Timeline.vue";
import TimelineElementLeft from "./timeline/TimelineElementLeft.vue";
import TimelineElementRight from "./timeline/TimelineElementRight.vue";

export default {
    components: { Timeline, TimelineElementLeft, TimelineElementRight },
    data: function() {
        return {
            experience: "Professional",
        }
    },
    methods: {
        onClickPersonal() {
            this.experience = "Personal";
        },
        onClickProfessional() {
            this.experience = "Professional";
        },
        age() {
            var today = new Date();
            var birthDate = new Date("01/07/1996");
            var age = today.getFullYear() - birthDate.getFullYear();
            var isBirthMonth = today.getMonth() === birthDate.getMonth();
            if (isBirthMonth && today.getDate() < birthDate.getDate()) {
                age--;
            }
            return age;
        }
    }
}
</script>

<style scoped>
div.theme-default-content:not(.full-width) {
    max-width: 100%;
}
</style>